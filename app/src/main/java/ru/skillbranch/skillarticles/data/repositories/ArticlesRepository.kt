package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import java.lang.Thread.sleep

interface IArticlesRepository {

    fun loadArticlesFromNetwork(start: Int = 0, size: Int): List<ArticleRes>

    fun insertResultIntoDb(articles: List<ArticleRes>)

    fun toggleBookmark(articleId: String)

    fun findTags(): LiveData<List<String>>

    fun findCategoriesData(): LiveData<List<CategoryData>>

    fun rawQueryArticles(filter: ArticleFilter): DataSource.Factory<Int, ArticleItem>

    fun incrementTagUseCount(tag: String)

}

object ArticlesRepository {

    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun allArticles(): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))
    }

    fun searchArticles(searchQuery: String): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.SearchArticle(::searchArticlesByTitle, searchQuery))
    }

    fun allBookmarked(): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.BookmarkedArticles(::findBookmarkedArticlesByRange))
    }

    fun searchBookmarkedArticles(searchQuery: String): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.SearchBookmark(::searchBookmarkedArticles, searchQuery))
    }

    private fun findArticlesByRange(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
            .drop(start)
            .take(size)

    private fun searchArticlesByTitle(start: Int, size: Int, queryTitle: String) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.title.contains(queryTitle,  true) }
            .drop(start)
            .take(size)
            .toList()

    private fun findBookmarkedArticlesByRange(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.isBookmark }
            .drop(start)
            .take(size)
            .toList()

    private fun searchBookmarkedArticles(start: Int, size: Int, query: String) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.isBookmark }
            .filter { it.title.contains(query, true) }
            .drop(start)
            .take(size)
            .toList()

    fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleItem> {
        return network.NETWORK_ARTICLE_ITEMS
                .drop(start)
                .take(size)
                .apply { sleep(500) }
    }

    fun insertArticlesToDb(articles: List<ArticleItem>) {
        local.LOCAL_ARTICLE_ITEMS.addAll(articles)
                .apply { sleep(500) }
    }

    fun updateBookmark(id: String, bookmark: Boolean) {
        local.updateBookmark(id, bookmark)
    }

}

class ArticlesDataFactory(val strategy: ArticleStrategy) : DataSource.Factory<Int, ArticleItem>() {
    override fun create(): DataSource<Int, ArticleItem> = ArticleDataSource(strategy)
}

class ArticleDataSource(val strategy: ArticleStrategy): PositionalDataSource<ArticleItem>() {

    override fun loadInitial(
            params: LoadInitialParams,
            callback: LoadInitialCallback<ArticleItem>
    ) {
        val result: List<ArticleItem> = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        Log.e("ArticlesRepository", "loadInitial: start > ${params.requestedStartPosition}  size > ${params.requestedLoadSize} resultSize >${result.size}")
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(
            params: LoadRangeParams,
            callback: LoadRangeCallback<ArticleItem>
    ) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        Log.e("ArticlesRepository", "loadRange: start > ${params.startPosition}  size > ${params.loadSize} resultSize > ${result.size}")
        callback.onResult(result)
    }

}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int) : List<ArticleItem>

    class AllArticles(
            private val itemProvider: (Int, Int) -> List<ArticleItem>
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size)
    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItem>,
        private val query: String
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> = itemProvider(start, size, query)
    }

    class BookmarkedArticles(
            private val itemProvider: (Int, Int) -> List<ArticleItem>
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size)
    }

    class SearchBookmark(private val itemProvider: (Int, Int, String) -> List<ArticleItem>, private val query: String) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size, query)
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
        if (search != null && !isHashtag) { qb.appendWhere("title LIKE '%$search%'") }

        if (search != null && isHashtag) {
            qb.innerJoin("article_tag_x_ref AS refs", "refs.a_id = id")
            qb.appendWhere("refs.t_id = '$search'")
        }

        if (isBookmark) { qb.appendWhere("is_bookmark = 1") }
        if (categories.isNotEmpty()) { qb.appendWhere("category_id IN (${categories.joinToString(",")})") }

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
        if (whereCondition.isNullOrEmpty()) whereCondition = "WHERE $condition "
        else whereCondition += "$logic $condition"
    }

    fun innerJoin(table: String, on: String) = apply {
        if (joinTables.isNullOrEmpty()) joinTables = "INNER JOIN $table ON $on"
        else joinTables += "INNER JOIN $table"
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