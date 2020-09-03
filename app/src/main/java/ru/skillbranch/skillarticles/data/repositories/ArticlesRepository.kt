package ru.skillbranch.skillarticles.data.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.skillbranch.skillarticles.data.local.DbManager
import ru.skillbranch.skillarticles.data.local.dao.*
import ru.skillbranch.skillarticles.data.local.entities.*
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import ru.skillbranch.skillarticles.extensions.data.toArticle
import ru.skillbranch.skillarticles.extensions.data.toArticleCounts

interface IArticlesRepository {

    suspend fun loadArticlesFromNetwork(start: String? = null, size: Int = 10): Int
    suspend fun insertArticlesToDb(articles: List<ArticleRes>)
    suspend fun toggleBookmark(articleId: String): Boolean

    fun findTags(): LiveData<List<String>>

    fun findCategoriesData(): LiveData<List<CategoryData>>

    fun rawQueryArticles(filter: ArticleFilter): DataSource.Factory<Int, ArticleItem>

    suspend fun incrementTagUseCount(tag: String)

}

object ArticlesRepository: IArticlesRepository {

    private val network = NetworkManager.api

    private var articlesDao = DbManager.db.articlesDao()
    private var articlesContentDao = DbManager.db.articleContentsDao()
    private var articleCountsDao = DbManager.db.articleCountsDao()
    private var categoriesDao = DbManager.db.categoriesDao()
    private var tagsDao = DbManager.db.tagsDao()
    private var articlePersonalDao = DbManager.db.articlePersonalInfosDao()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setupTestDao(
        articlesDao: ArticlesDao,
        articleCountsDao: ArticleCountsDao,
        categoriesDao: CategoriesDao,
        tagsDao: TagsDao,
        articlePersonalDao: ArticlePersonalInfosDao
    ) {
        this.articlesDao = articlesDao
        this.articleCountsDao = articleCountsDao
        this.categoriesDao = categoriesDao
        this.tagsDao = tagsDao
        this.articlePersonalDao = articlePersonalDao
    }

    override suspend fun loadArticlesFromNetwork(start: String?, size: Int) : Int {
        val items = network.articles(start, size)
        if (items.isNotEmpty()) insertArticlesToDb(items)
        return items.size
    }

    override suspend fun insertArticlesToDb(articles: List<ArticleRes>) {
        articlesDao.upsert(articles.map { it.data.toArticle() })
        articleCountsDao.upsert(articles.map{ it.counts.toArticleCounts() })

        val refs = articles.map { it.data }
            .fold(mutableListOf<Pair<String, String>>()) { acc, res ->
                acc.also { list -> list.addAll(res.tags.map { res.id to it }) }
            }

        val tags = refs.map { it.second }
            .distinct()
            .map { Tag(it) }

        val categories = articles.map { it.data.category }

        categoriesDao.insert(categories)
        tagsDao.insert(tags)
        tagsDao.insertRefs(refs.map { ArticleTagXRef(it.first, it.second) })
    }

    override suspend fun toggleBookmark(articleId: String): Boolean {
        return articlePersonalDao.toggleBookmarkOrInsert(articleId)
    }

    override fun findTags(): LiveData<List<String>> {
        return tagsDao.findTags()
    }

    override fun findCategoriesData(): LiveData<List<CategoryData>> {
        return categoriesDao.findAllCategoriesData()
    }

    override fun rawQueryArticles(filter: ArticleFilter): DataSource.Factory<Int, ArticleItem> {
        return articlesDao.findArticlesByRaw(SimpleSQLiteQuery(filter.toQuery()))
    }

    override suspend fun incrementTagUseCount(tag: String) {
        tagsDao.incrementTagUseCount(tag)
    }

    suspend fun findLastArticleId(): String? = articlesDao.findLastArticleId()

    suspend fun fetchArticleContent(articleId: String) {
        val content: ArticleContent = network.loadArticleContent(articleId)
        articlesContentDao.insert(content)
    }

}

data class ArticleFilter(
    val search: String? = null,
    val isBookmark: Boolean = false,
    val categories: List<String> = listOf(),
    val isHashtag: Boolean = false
) {
    fun toQuery(): String {
        val qb = QueryBuilder()
        qb.table("ArticleItem")
        if (search != null && !isHashtag) {
            qb.appendWhere("title LIKE '%${search}%'")
        }

        if (search != null && isHashtag) {
            qb.innerJoin("article_tag_x_ref AS refs", "refs.a_id = id")
            qb.appendWhere("refs.t_id = '$search'")
        }

        if (isBookmark) {
            qb.appendWhere("is_bookmark = 1")
        }

        if (categories.isNotEmpty()) {
            qb.appendWhere("category_id IN (${categories.joinToString(",")})")
        }

        qb.orderBy("date")
        return qb.build()
    }
}

class QueryBuilder {

    private var table: String? = null
    private var selectColumns: String = "*"
    private var joinTables: String? = null
    private var whereCondition: String? = null
    private var order: String? = null

    fun table(table: String) = apply {
        this.table = table
    }

    fun orderBy(column: String, isDesc: Boolean = true) = apply {
        order = " ORDER BY $column ${if(isDesc) "DESC" else "ASC"}"
    }

    fun appendWhere(condition: String, logic: String = "AND") = apply {
        if (whereCondition.isNullOrEmpty()) whereCondition = " WHERE $condition "
        else whereCondition += " $logic $condition"
    }

    fun innerJoin(table: String, on: String) = apply {
        if (joinTables.isNullOrEmpty()) joinTables = " INNER JOIN $table ON $on"
        else joinTables += " INNER JOIN $table"
    }

    fun build(): String {
        check(table != null) {"table must be not null" }
        val strBuilder = StringBuilder("SELECT ")
            .append("$selectColumns ")
            .append("FROM $table")

        joinTables?.let { strBuilder.append(joinTables) }

        whereCondition?.let { strBuilder.append(it) }
        order?.let { strBuilder.append(it) }
        return strBuilder.toString()
    }

}