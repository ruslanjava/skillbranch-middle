package ru.skillbranch.skillarticles.data.delegates

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.squareup.moshi.JsonAdapter
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PrefLiveObjDelegate<T>(
    private val fieldKey: String,
    private val adapter: JsonAdapter<T>,
    private val preferences: SharedPreferences) :
    ReadOnlyProperty<PrefManager, LiveData<T?>> {

    private var storedValue: LiveData<T?>? = null

    override fun getValue(thisRef: PrefManager, property: KProperty<*>): LiveData<T?> {
        if (storedValue == null) {
            storedValue = SharedPreferencesLiveData(preferences, fieldKey, adapter)
        }
        return storedValue!!
    }

}

internal class SharedPreferencesLiveData<T>(
    private val sharedPrefs: SharedPreferences,
    var key: String,
    var adapter: JsonAdapter<T>
): LiveData<T?>() {

    private val preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
        if (shKey == key) {
            value = readValue()
        }
    }

    override fun onActive() {
        super.onActive()
        value = readValue()
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onInactive()
    }

    private fun readValue(): T? {
        return sharedPrefs.getString(key, null)?.let { adapter.fromJson(it) }
    }

}