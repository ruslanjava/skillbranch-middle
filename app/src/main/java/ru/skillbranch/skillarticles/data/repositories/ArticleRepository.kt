package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.*
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.entities.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.models.*
import java.lang.Thread.sleep
import kotlin.math.abs

object ArticleRepository {

    private val network = NetworkDataHolder
    private val preferences = PrefManager

    fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?> {
        return MutableLiveData(emptyList())
    }

    fun getArticle(articleId: String): LiveData<ArticleData?> {
        return MutableLiveData(null)
    }

    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return MutableLiveData(null)
    }

    fun getAppSettings(): LiveData<AppSettings> = preferences.getAppSettings() //from preferences

    fun updateSettings(appSettings: AppSettings) {
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
    }

    fun isAuth(): MutableLiveData<Boolean> = preferences.isAuth()

    fun allComments(articleId: String, totalCount: Int) = CommentsDataFactory(itemProvider = ::loadCommentsByRange,
            articleId = articleId, totalCount = totalCount)

    fun loadCommentsByRange(slug: String?, size: Int, articleId: String) : List<CommentItemData> {
        val data = network.commentsData.getOrElse(articleId) {
            mutableListOf()
        }
        return when {
            slug == null -> data.take(size)

            size > 0 -> data.dropWhile { it.slug != slug }
                    .drop(1)
                    .take(size)

            size < 0 -> data
                    .dropLastWhile { it.slug != slug }
                    .dropLast(1)
                    .takeLast(abs(size))

            else -> emptyList()
        }.apply {
            sleep(500)
        }
    }

    fun sendComment(articleId: String, comment: String, answerToSlug: String?) {
        network.sendMessage(articleId, comment, answerToSlug,
                User("777", "John Doe", "https://skill-branch.ru/img/mail/bot/android-category.png")
        )
    }

    fun updateBookmark(id: String, bookmark: Boolean) {
        local.updateBookmark(id, bookmark)
    }

}

class CommentsDataFactory(
        private val itemProvider: (String?, Int, String) -> List<CommentItemData>,
        private val articleId: String,
        private val totalCount: Int
): DataSource.Factory<String?, CommentItemData>() {
    override fun create(): DataSource<String?, CommentItemData> = CommentsDataSource(itemProvider, articleId, totalCount)
}

class CommentsDataSource(
        private val itemProvider: (String?, Int, String) -> List<CommentItemData>,
        private val articleId: String,
        private val totalCount: Int
): ItemKeyedDataSource<String, CommentItemData>() {

    override fun loadInitial(
            params: LoadInitialParams<String>,
            callback: LoadInitialCallback<CommentItemData>
    ) {
        val result = itemProvider(params.requestedInitialKey, params.requestedLoadSize, articleId)
        Log.e("ArticleRepository", "loadInitial: key > ${params.requestedInitialKey} size > ${result.size} totalCount > $totalCount")
        callback.onResult(
                if (totalCount > 0) result else emptyList(),
                0,
                totalCount
        )
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, params.requestedLoadSize, articleId)
        Log.e("ArticleRepository", "loadAfter: key > ${params.key} size > ${result.size} ")
        callback.onResult(result)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, -params.requestedLoadSize, articleId)
        Log.e("ArticleRepository", "loadBefore: key > ${params.key} size > ${result.size} ")
        callback.onResult(result)
    }

    override fun getKey(item: CommentItemData): String = item.slug



}