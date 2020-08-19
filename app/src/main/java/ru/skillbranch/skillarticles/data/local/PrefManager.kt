package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object PrefManager {

    val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isAuth by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    val isAuthLive: LiveData<Boolean> by PrefLiveDelegate("isAuth", false, preferences)

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

    fun setAuth(auth: Boolean) {
        authLiveData.postValue(auth)
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

internal class PrefLiveDelegate<T>(
    private val fieldKey: String,
    private val defaultValue: T,
    private val preferences: SharedPreferences
): ReadOnlyProperty<Any?, LiveData<T>> {

    private var storedValue: LiveData<T>? = null

    override fun getValue(thisRef: Any?, propery: KProperty<*>): LiveData<T> {
        if (storedValue == null) {
            storedValue = SharedPreferenceLiveData(preferences, fieldKey, defaultValue)
        }
        return storedValue!!
    }

}

internal class SharedPreferenceLiveData<T>(
    var sharedPrefs: SharedPreferences,
    var key: String,
    var defValue: T
): LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
            if (shKey == key) {
                value = readValue(defValue)
            }
        }

    override fun onActive() {
        super.onActive()
        value = readValue(defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    private fun readValue(defaultValue: T): T {
        return when (defaultValue) {
            is Int -> sharedPrefs.getInt(key, defaultValue as Int) as T
            is Long -> sharedPrefs.getLong(key, defaultValue as Long) as T
            is Float -> sharedPrefs.getFloat(key, defaultValue as Float) as T
            is String -> sharedPrefs.getString(key, defaultValue as String) as T
            is Boolean -> sharedPrefs.getBoolean(key, defaultValue as Boolean) as T
            else -> error("This type $defaultValue can't be stored int Preferences")
        }
    }

}