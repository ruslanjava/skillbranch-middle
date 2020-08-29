package ru.skillbranch.skillarticles.data.local.dao

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

@Dao
interface ArticlesDao : BaseDao<Article> {

    @Transaction
    suspend fun upsert(list: List<Article>) {
        insert(list)
            .mapIndexed { index, recordResult -> if (recordResult == -1L) list[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }

    @Query("SELECT * FROM articles")
    fun findArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE id = :id")
    fun findArticleById(id: String): LiveData<Article>

    @Query("SELECT * FROM ArticleItem")
    fun findArticleItems(): LiveData<List<ArticleItem>>

    @Delete
    override suspend fun delete(article: Article)

    @Query("SELECT * FROM ArticleItem WHERE category_id IN (:categoryIds)")
    fun findArticleItemsByCategoryIds(categoryIds: List<String>): List<ArticleItem>

    @Query("SELECT * FROM ArticleItem INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id WHERE refs.t_id = :tag")
    fun findArticlesByTagId(tag: String): List<ArticleItem>

    @RawQuery(observedEntities = [ArticleItem::class])
    fun findArticlesByRaw(simpleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, ArticleItem>

    @Query("SELECT * FROM ArticleFull WHERE id = :articleId")
    fun findFullArticle(articleId: String): LiveData<ArticleFull>

    @Query("SELECT t_id FROM article_tag_x_ref WHERE a_id = :articleId")
    fun findTagsByArticleId(articleId: String): List<String>

}