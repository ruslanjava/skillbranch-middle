package ru.skillbranch.skillarticles.data.remote.res

import ru.skillbranch.skillarticles.data.local.entities.Category
import java.util.*

data class ArticleDataRes(
    val id: String,
    val date: Date,
    val author: AuthorRes,
    val title: String,
    val description: String,
    val poster: String,
    val category: Category,
    val tags: List<String> = listOf()
)