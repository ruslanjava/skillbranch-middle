package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.ArticleCounts
import ru.skillbranch.skillarticles.data.remote.res.ArticleCountsRes
import java.util.*

fun ArticleCountsRes.toArticleCounts(): ArticleCounts = ArticleCounts(
    articleId = articleId,
    likes = likes,
    comments = comments,
    readDuration = readDuration,
    updatedAt = Date()
)