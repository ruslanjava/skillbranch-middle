package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.local.entities.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState

fun ArticleState.toAppSettings() : AppSettings {
    return AppSettings(isDarkMode, isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(
        articleId = "1",
        isLike = isLike,
        isBookmark = isBookmark
    )
}