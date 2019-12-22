package ru.skillbranch.skillarticles.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import java.util.*

object LocalDataHolder {

    private var isDelay = true

    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        ctx.getSharedPreferences("local_data", Context.MODE_PRIVATE)
    }

    private val defaultSettingsValue: AppSettings by lazy {
        AppSettings(
                prefs.getBoolean("is_dark_mode", false),
                prefs.getBoolean("is_big_text", false)
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val settings: MutableLiveData<AppSettings> = MutableLiveData(defaultSettingsValue)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleInfo: MutableLiveData<ArticlePersonalInfo> = MutableLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleData: MutableLiveData<ArticleData> = MutableLiveData()

    fun getAppSettings(): LiveData<AppSettings> {
        return settings
    }

    fun updateAppSettings(appSettings: AppSettings) {
        with(prefs.edit()) {
            putBoolean("is_dark_mode", appSettings.isDarkMode)
            putBoolean("is_big_text", appSettings.isBigText)
            apply()
        }
        settings.value = appSettings
    }

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        GlobalScope.launch {
            if (isDelay) delay(2000)
            articleData.postValue(ArticleData(
                    title = "CoordinatorLayout Basic",
                    category = "Android",
                    categoryIcon = R.drawable.logo,
                    date = Date()
            ))
        }
        return articleData
    }

    fun findArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        GlobalScope.launch {
            if (isDelay) delay(1000)
            articleInfo.postValue(ArticlePersonalInfo())
        }
        return articleInfo
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        articleInfo.value = info
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun disableDelay() {
        isDelay = false
    }

}