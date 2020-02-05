package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PrefManager(context: Context) {

    private val preferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getBoolean(key: String, defaultValue: Boolean) : Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String) : String {
        return preferences.getString(key, defaultValue)!!
    }

    fun getFloat(key: String, defaultValue: Float) : Float {
        return preferences.getFloat(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int) : Int {
        return preferences.getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long) : Long {
        return preferences.getLong(key, defaultValue)
    }

    fun setBoolean(key: String, newValue: Boolean?, defaultValue: Boolean) {
        return preferences.edit().putBoolean(key, newValue ?: defaultValue).apply()
    }

    fun setString(key: String, newValue: String?, defaultValue: String) {
        return preferences.edit().putString(key, newValue ?: defaultValue).apply()
    }

    fun setFloat(key: String, newValue: Float?, defaultValue: Float) {
        return preferences.edit().putFloat(key, newValue ?: defaultValue).apply()
    }

    fun setInt(key: String, newValue: Int?, defaultValue: Int) {
        return preferences.edit().putInt(key, newValue ?: defaultValue).apply()
    }

    fun setLong(key: String, newValue: Long?, defaultValue: Long) {
        return preferences.edit().putLong(key, newValue ?: defaultValue).apply()
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

}