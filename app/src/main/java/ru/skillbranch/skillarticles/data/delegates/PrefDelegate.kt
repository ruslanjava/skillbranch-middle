package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        return when (defaultValue) {
            is Boolean -> thisRef.preferences.getBoolean(property.name, defaultValue as Boolean) as T
            is String -> thisRef.preferences.getString(property.name, defaultValue as String) as T
            is Float -> thisRef.preferences.getFloat(property.name, defaultValue as Float) as T
            is Int -> thisRef.preferences.getInt(property.name, defaultValue as Int) as T
            is Long -> thisRef.preferences.getLong(property.name, defaultValue as Long) as T
            else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
        }
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        when (defaultValue) {
            is Boolean -> {
                val savedValue = value ?: defaultValue
                thisRef.preferences.edit().putBoolean(property.name, savedValue as Boolean).apply()
            }
            is String -> {
                val savedValue: String = (value ?: defaultValue) as String
                thisRef.preferences.edit().putString(property.name, savedValue).apply()
            }
            is Float -> {
                val savedValue: Float = (value ?: defaultValue) as Float
                thisRef.preferences.edit().putFloat(property.name, savedValue).apply()
            }
            is Long -> {
                val savedValue: Long = (value ?: defaultValue) as Long
                thisRef.preferences.edit().putLong(property.name, savedValue).apply()
            }
            is Int -> {
                val savedValue: Int = (value ?: defaultValue) as Int
                thisRef.preferences.edit().putInt(property.name, savedValue).apply()
            }
            else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
        }
    }

}