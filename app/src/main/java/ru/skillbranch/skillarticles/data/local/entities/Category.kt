package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_categories")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val icon: String,
    val title: String
)

data class CategoryData(
    @ColumnInfo(name = "category_id")
    val categoryId: String,

    val icon: String,
    val title: String,

    @ColumnInfo(name = "articles_count")
    val articlesCount: Int = 0
)
