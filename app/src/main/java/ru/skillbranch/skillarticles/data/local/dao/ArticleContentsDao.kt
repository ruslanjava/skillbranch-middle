package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.*
import ru.skillbranch.skillarticles.data.local.entities.ArticleContent

@Dao
interface ArticleContentsDao: BaseDao<ArticleContent> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: ArticleContent): Long

    @Query("DELETE from article_contents WHERE article_id = :articleId")
    suspend fun remove(articleId: String)

    // для RepositoryTest1.kt
    @Query("select * from article_contents")
    suspend fun findArticlesContentsTest(): List<ArticleContent>

}