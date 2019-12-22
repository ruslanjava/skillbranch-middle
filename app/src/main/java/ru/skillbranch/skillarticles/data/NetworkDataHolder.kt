package ru.skillbranch.skillarticles.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NetworkDataHolder {

    private var isDelay = true

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val content: MutableLiveData<List<Any>> = MutableLiveData()

    fun loadArticleContent(articleId: String): LiveData<List<Any>?> {
        GlobalScope.launch {
            if (isDelay) delay(5000)
            content.postValue(listOf(longText))
        }
        return content
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun disableDelay() {
        isDelay = false
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val longText: String = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt 
        ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco 
        laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in 
        voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat 
        cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
    """.trimIndent()

}