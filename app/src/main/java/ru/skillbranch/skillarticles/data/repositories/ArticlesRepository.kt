package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import java.lang.Thread.sleep

object ArticlesRepository {

    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun allArticles(): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))
    }

    fun searchArticles(searchQuery: String): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.SearchArticle(::searchArticlesByTitle, searchQuery))
    }

    fun allBookmarked(): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.BookmarkedArticles(::findBookmarkedArticlesByRange))
    }

    fun searchBookmarkedArticles(searchQuery: String): ArticlesDataFactory {
        return ArticlesDataFactory(ArticleStrategy.SearchBookmark(::searchBookmarkedArticles, searchQuery))
    }

    private fun findArticlesByRange(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
            .drop(start)
            .take(size)

    private fun searchArticlesByTitle(start: Int, size: Int, queryTitle: String) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.title.contains(queryTitle,  true) }
            .drop(start)
            .take(size)
            .toList()

    private fun findBookmarkedArticlesByRange(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.isBookmark }
            .drop(start)
            .take(size)
            .toList()

    private fun searchBookmarkedArticles(start: Int, size: Int, query: String) = local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.isBookmark }
            .filter { it.title.contains(query, true) }
            .drop(start)
            .take(size)
            .toList()

    fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleItem> {
        return network.NETWORK_ARTICLE_ITEMS
                .drop(start)
                .take(size)
                .apply { sleep(500) }
    }

    fun insertArticlesToDb(articles: List<ArticleItem>) {
        local.LOCAL_ARTICLE_ITEMS.addAll(articles)
                .apply { sleep(500) }
    }

    fun updateBookmark(id: String, bookmark: Boolean) {
        local.updateBookmark(id, bookmark)
    }

}

class ArticlesDataFactory(val strategy: ArticleStrategy) : DataSource.Factory<Int, ArticleItem>() {
    override fun create(): DataSource<Int, ArticleItem> = ArticleDataSource(strategy)
}

class ArticleDataSource(val strategy: ArticleStrategy): PositionalDataSource<ArticleItem>() {

    override fun loadInitial(
            params: LoadInitialParams,
            callback: LoadInitialCallback<ArticleItem>
    ) {
        val result: List<ArticleItem> = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        Log.e("ArticlesRepository", "loadInitial: start > ${params.requestedStartPosition}  size > ${params.requestedLoadSize} resultSize >${result.size}")
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(
            params: LoadRangeParams,
            callback: LoadRangeCallback<ArticleItem>
    ) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        Log.e("ArticlesRepository", "loadRange: start > ${params.startPosition}  size > ${params.loadSize} resultSize > ${result.size}")
        callback.onResult(result)
    }

}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int) : List<ArticleItem>

    class AllArticles(
            private val itemProvider: (Int, Int) -> List<ArticleItem>
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size)
    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItem>,
        private val query: String
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> = itemProvider(start, size, query)
    }

    class BookmarkedArticles(
            private val itemProvider: (Int, Int) -> List<ArticleItem>
    ): ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size)
    }

    class SearchBookmark(private val itemProvider: (Int, Int, String) -> List<ArticleItem>, private val query: String) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem>  = itemProvider(start, size, query)
    }

}