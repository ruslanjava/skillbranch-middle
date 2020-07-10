package ru.skillbranch.skillarticles.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.Category
import ru.skillbranch.skillarticles.data.local.entities.CategoryData

@Dao
interface CategoriesDao: BaseDao<Category> {

    @Transaction
    fun upsert(category: Category) {
        val newId = insert(category)
        if (newId <= 0) {
            update(category)
        }
    }

    @Transaction
    fun upsert(list: List<Category>) {
        insert(list)
            .mapIndexed { index, recordResult -> if (recordResult == -1L) list[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }

    @Query("""
        SELECT category.title AS title, category.icon, category.category_id AS category_id, 
        COUNT(article.category_id) AS articles_count 
        FROM article_categories AS category
        INNER JOIN articles AS article ON category.category_id = article.category_id
        GROUP BY category.category_id
        ORDER BY articles_count DESC
    """)
    fun findAllCategoriesData(): LiveData<List<CategoryData>>

}