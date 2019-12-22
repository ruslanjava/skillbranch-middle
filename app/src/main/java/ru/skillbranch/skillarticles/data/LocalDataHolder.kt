package ru.skillbranch.skillarticles.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.skillbranch.skillarticles.App

object LocalDataHolder {

    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        ctx.getSharedPreferences("local_data", Context.MODE_PRIVATE)
    }

    private val defaultValue: AppSettings by lazy {
        AppSettings(
                isDarkMode = prefs.getBoolean("is_dark_mode", false),
                isBigText = prefs.getBoolean("is_big_text", false)
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val settings: MutableLiveData<AppSettings> = MutableLiveData(defaultValue)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleInfo: MutableLiveData<ArticlePersonalInfo> = MutableLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleData: MutableLiveData<ArticleData> = MutableLiveData()

    fun getAppSettings(): LiveData<AppSettings> {
        return settings
    }

    fun updateAppSettings(appSettings: AppSettings) {
        with (prefs.edit()) {
            putBoolean("is_dark_mode", appSettings.isDarkMode)
            putBoolean("is_big_text", appSettings.isBigText)
            apply()
        }
        settings.value = appSettings
    }

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun findArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}