package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {

    val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isDarkMode by PrefDelegate(false)
    private var bigText by PrefDelegate(false)

    private val appSettingsLiveData: MutableLiveData<AppSettings> by lazy {
        val result = MutableLiveData<AppSettings>()
        result.postValue(AppSettings().copy(isDarkMode = isDarkMode ?: false, isBigText = bigText ?: false))
        return@lazy result
    }

    private val authLiveData: MutableLiveData<Boolean> by lazy {
        val result = MutableLiveData<Boolean>()
        result.postValue(false)
        return@lazy result
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> {
        return appSettingsLiveData
    }

    fun isAuth(): MutableLiveData<Boolean> {
        return authLiveData
    }

    fun setAuth(auth: Boolean) {
        authLiveData.postValue(auth)
    }

    fun updateSettings(appSettings: AppSettings) {
        isDarkMode = appSettings.isDarkMode
        bigText = appSettings.isBigText
        appSettingsLiveData.postValue(appSettings)
    }

}