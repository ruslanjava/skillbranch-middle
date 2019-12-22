package ru.skillbranch.skillarticles.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkDataHolder {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val content: MutableLiveData<List<Any>> = MutableLiveData()

    fun loadArticleContent(articleId: String): LiveData<List<Any>?> {
        return content
    }

}