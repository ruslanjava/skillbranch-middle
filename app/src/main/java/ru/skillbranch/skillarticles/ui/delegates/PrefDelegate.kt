package ru.skillbranch.skillarticles.ui.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        return when (defaultValue) {
            is Boolean -> thisRef.getBoolean(property.name, defaultValue as Boolean) as T
            is String -> thisRef.getString(property.name, defaultValue as String) as T
            is Float -> thisRef.getFloat(property.name, defaultValue as Float) as T
            is Int -> thisRef.getInt(property.name, defaultValue as Int) as T
            is Long -> thisRef.getLong(property.name, defaultValue as Long) as T
            else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
        }
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        when (defaultValue) {
            is Boolean -> thisRef.setBoolean(property.name, value as Boolean, defaultValue)
            is String -> thisRef.setString(property.name, value as String, defaultValue)
            is Float -> thisRef.setFloat(property.name, value as Float, defaultValue)
            is Long -> thisRef.setLong(property.name, value as Long, defaultValue)
            is Int -> thisRef.setInt(property.name, value as Int, defaultValue)
            else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
        }
    }

}