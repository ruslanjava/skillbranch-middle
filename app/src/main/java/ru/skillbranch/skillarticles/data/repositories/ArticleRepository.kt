package ru.skillbranch.skillarticles.data.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.local.DbManager
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.dao.ArticleContentsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticleCountsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlePersonalInfosDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlesDao
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.models.*
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.MessageReq
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.extensions.data.toArticleContent

interface IArticleRepository {

    fun findArticle(articleId: String): LiveData<ArticleFull>
    fun getAppSettings(): LiveData<AppSettings>
    fun isAuth(): MutableLiveData<Boolean>
    fun updateSettings(copy: AppSettings)

    suspend fun sendMessage(articleId: String, text: String, answerToSlug: String?)
    suspend fun toggleLike(articleId: String)
    suspend fun toggleBookmark(articleId: String): Boolean
    suspend fun decrementLike(articleId: String)
    suspend fun incrementLike(articleId: String)

    suspend fun fetchArticleContent(articleId: String)

    suspend fun addBookmark(articleId: String)
    suspend fun removeBookmark(articleId: String)

    fun findArticleCommentCount(articleId: String): LiveData<Int>
    fun loadAllComments(
        articleId: String,
        totalCount: Int,
        errHandler: (Throwable) -> Unit
    ): CommentsDataFactory

}

object ArticleRepository : IArticleRepository {

    private val network = NetworkManager.api
    private val preferences = PrefManager

    private var articlesDao = DbManager.db.articlesDao()
    private var articlePersonalDao = DbManager.db.articlePersonalInfosDao()
    private var articleCountsDao = DbManager.db.articleCountsDao()
    private var articleContentDao = DbManager.db.articleContentsDao()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setupTestDao(
        articlesDao: ArticlesDao,
        articlePersonalDao: ArticlePersonalInfosDao,
        articleCountsDao: ArticleCountsDao,
        articleContentDao: ArticleContentsDao
    ) {
        this.articlesDao = articlesDao
        this.articlePersonalDao = articlePersonalDao
        this.articleCountsDao = articleCountsDao
        this.articleContentDao = articleContentDao
    }

    override fun findArticle(articleId: String): LiveData<ArticleFull> {
        return articlesDao.findFullArticle(articleId)
    }

    override fun getAppSettings(): LiveData<AppSettings> = preferences.getAppSettings() //from preferences

    override suspend fun toggleLike(articleId: String) {
        articlePersonalDao.toggleLikeOrInsert(articleId)
    }

    override suspend fun toggleBookmark(articleId: String): Boolean {
        return articlePersonalDao.isBookmarked(articleId)
    }

    override fun updateSettings(appSettings: AppSettings) {
        preferences.updateSettings(appSettings)
    }

    override suspend fun fetchArticleContent(articleId: String) {
        val content = network.loadArticleContent(articleId)
        articleContentDao.insert(content.toArticleContent())
    }

    override fun findArticleCommentCount(articleId: String): LiveData<Int> {
        return articleCountsDao.getCommentsCount(articleId)
    }

    override fun isAuth(): MutableLiveData<Boolean> = preferences.isAuth()

    override suspend fun decrementLike(articleId: String) {
        // check auth locally
        if (preferences.accessToken.isEmpty()) {
            articleCountsDao.decrementLike(articleId)
        }

        try {
            val res = network.decrementLike(articleId, preferences.accessToken)
            articleCountsDao.updateLike(articleId, res.likeCount)
        } catch (e: Throwable) {
            articleCountsDao.decrementLike(articleId)
            throw e
        }
    }

    override suspend fun incrementLike(articleId: String) {
        if (preferences.accessToken.isEmpty()) {
            articleCountsDao.incrementLike(articleId)
        }

        try {
            val res = network.incrementLike(articleId, preferences.accessToken)
            articleCountsDao.updateLike(articleId, res.likeCount)
        } catch (e: Throwable) {
            articleCountsDao.decrementLike(articleId)
            throw e
        }
    }

    override suspend fun sendMessage(articleId: String, message: String, answerToMessageId: String?) {
        val (_, messageCount) = network.sendMessage(
            articleId,
            MessageReq(message, answerToMessageId),
            preferences.accessToken
        )
        articleCountsDao.updateCommentsCount(articleId, messageCount)
    }

    suspend fun refreshCommentsCount(articleId: String) {
        val counts = network.loadArticleCounts(articleId)
        articleCountsDao.updateCommentsCount(articleId, counts)
    }

    override suspend fun addBookmark(articleId: String) {
        articlePersonalDao.addToBookmark(articleId)
        if (preferences.accessToken.isEmpty()) {
            return
        }
        network.addBookmark(articleId, preferences.accessToken)
    }

    override suspend fun removeBookmark(articleId: String) {
        articlePersonalDao.removeFromBookmark(articleId)
        if (preferences.accessToken.isEmpty()) {
            return
        }
        network.removeBookmark(articleId, preferences.accessToken)
    }

    override fun loadAllComments(articleId: String, totalCount: Int, errHandler: (Throwable) -> Unit) =
        CommentsDataFactory(
            itemProvider = network,
            articleId = articleId,
            totalCount = totalCount,
            errHandler = errHandler
        )

}

class CommentsDataFactory(
        private val itemProvider: RestService,
        private val articleId: String,
        private val totalCount: Int,
        private val errHandler: (Throwable) -> Unit
): DataSource.Factory<String?, CommentRes>() {
    override fun create(): DataSource<String?, CommentRes> = CommentsDataSource(
        itemProvider, articleId, totalCount, errHandler
    )
}

class CommentsDataSource(
    private val itemProvider: RestService,
    private val articleId: String,
    private val totalCount: Int,
    private val errHandler: (Throwable) -> Unit
): ItemKeyedDataSource<String, CommentRes>() {

    override fun loadInitial(
            params: LoadInitialParams<String>,
            callback: LoadInitialCallback<CommentRes>
    ) {
        try {
            // sync call execute
            val result = itemProvider.loadComments(
                articleId,
                params.requestedInitialKey,
                params.requestedLoadSize
            ).execute()

            callback.onResult(
                if (totalCount > 0) result.body()!! else emptyList(),
                0,
                totalCount
            )
        } catch (e: Exception) {
            // handle network errors in viewModel
            errHandler(e)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId,
                params.key,
                params.requestedLoadSize
            ).execute()
            callback.onResult(result.body()!!)
        } catch (e: Exception) {
            errHandler(e)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId,
                params.key,
                -params.requestedLoadSize
            ).execute()
            callback.onResult(result.body()!!)
        } catch (e: Exception) {
            errHandler(e)
        }
    }

    override fun getKey(item: CommentRes): String = item.id

}