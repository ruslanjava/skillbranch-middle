package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.remote.res.ArticleDataRes
import java.util.*

fun ArticleDataRes.toArticle(): Article = Article(
    id = this.id,
    categoryId = this.category.categoryId,
    title = this.title,
    author = this.author,
    date = this.date,
    description = this.description,
    poster = this.poster,
    updatedAt = Date()
)