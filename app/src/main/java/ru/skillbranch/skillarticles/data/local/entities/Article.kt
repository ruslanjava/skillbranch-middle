package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val poster: String
)