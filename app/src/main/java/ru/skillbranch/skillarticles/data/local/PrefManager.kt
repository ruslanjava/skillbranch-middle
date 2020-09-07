package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.JsonConverter.moshi
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveObjDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefObjDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.User

object PrefManager {

    val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isAuth by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    var accessToken by PrefDelegate("")
    var refreshToken by PrefDelegate("")

    var profile: User? by PrefObjDelegate(moshi.adapter(User::class.java))

    val isAuthLive: LiveData<Boolean> by lazy {
        val token: LiveData<String> by PrefLiveDelegate("accessToken", "", preferences)
        token.map {
            it.isNotEmpty()
        }
    }

    val profileLive: LiveData<User?> by PrefLiveObjDelegate(
        "profile",
        moshi.adapter(User::class.java),
        preferences
    )

    private val appSettingsLiveData: MutableLiveData<AppSettings> = MediatorLiveData<AppSettings>().apply {
        val isDarkModeLive: LiveData<Boolean> by PrefLiveDelegate("isdarkMode", false, preferences)
        val isBigTextLive: LiveData<Boolean> by PrefLiveDelegate("isBigText", false, preferences)

        value = AppSettings()

        addSource(isDarkModeLive) {
            value = value!!.copy(isDarkMode = it)
        }

        addSource(isBigTextLive) {
            value = value!!.copy(isBigText = it)
        }
    }.distinctUntilChanged()

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

    fun updateSettings(appSettings: AppSettings) {
        isDarkMode = appSettings.isDarkMode
        isBigText = appSettings.isBigText
        appSettingsLiveData.postValue(appSettings)
    }

    fun <T> MutableLiveData<T>.distinctUntilChanged(): MutableLiveData<T> = MediatorLiveData<T>().also { mediator ->
        mediator.addSource(this, object : Observer<T> {
            private var isInitialized = false
            private var previousValue: T? = null

            override fun onChanged(newValue: T?) {
                val wasInitialized = isInitialized
                if (!isInitialized) {
                    isInitialized = true
                }
                if(!wasInitialized || newValue != previousValue) {
                    previousValue = newValue
                    mediator.postValue(newValue)
                }
            }
        })
    }

}