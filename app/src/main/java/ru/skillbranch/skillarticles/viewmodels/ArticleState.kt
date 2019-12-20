package ru.skillbranch.skillarticles.viewmodels

class ArticleState constructor(val isLike: Boolean, val isBookmark: Boolean) {

    var isDarkMode: Boolean? = null
    var isBigText: Boolean? = null

    constructor(isLike: Boolean, isBookmark: Boolean, isDarkMode: Boolean, isBigText: Boolean) :
            this(isLike, isBookmark) {
        this.isDarkMode = isDarkMode
        this.isBigText = isBigText
    }

}