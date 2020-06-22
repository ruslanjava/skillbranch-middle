package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.ArticleCounts

@Dao
interface ArticleCountsDao: BaseDao<ArticleCounts> {

    @Transaction
    fun upsert(list: List<ArticleCounts>) {
        insert(list)
            .mapIndexed { index, recortResult -> if (recortResult == -1L) list[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }

    /*
    fun incrementLikeOrInsert(articleId: String)

    fun incrementLike(articleId: String): Int

    fun decrementLikee(articleId: String): Int

    fun incrementCommentsCount(articleId: String)

    fun getCommentsCount(articleId: String): Int
     */

}