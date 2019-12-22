package ru.skillbranch.skillarticles.data

import java.util.*

data class ArticleData(
        val shareLink: String? = null,
        val title: String?,
        val category: String?,
        val categoryIcon: Any? = null,
        val date: Date
)