package ru.skillbranch.skillarticles.data.delegates

import com.squareup.moshi.JsonAdapter
import ru.skillbranch.skillarticles.data.local.PrefManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

    private var storedValue: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        if (storedValue == null) {
            with(thisRef.preferences) {
                storedValue = when (defaultValue) {
                    is Boolean -> getBoolean(property.name, defaultValue as Boolean) as T
                    is String -> getString(property.name, defaultValue as String) as T
                    is Float -> getFloat(property.name, defaultValue as Float) as T
                    is Int -> getInt(property.name, defaultValue as Int) as T
                    is Long -> getLong(property.name, defaultValue as Long) as T
                    else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
                }
            }
        }
        return storedValue
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        if (storedValue != value) {
            with (thisRef.preferences.edit()) {
                when (value) {
                    is Boolean -> putBoolean(property.name, value)
                    is String -> putString(property.name, value)
                    is Float -> putFloat(property.name, value)
                    is Long -> putLong(property.name, value)
                    is Int -> putInt(property.name, value)
                    else -> throw IllegalArgumentException("Unsupported value: $defaultValue")
                }
                apply()
            }
        }
        storedValue = value
    }

}

class PrefObjDelegate<T>(
    private val adapter: JsonAdapter<T>
) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        prop: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key = prop.name
        return object: ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) {
                    storedValue = thisRef.preferences.getString(key, null)?.let { adapter.fromJson(it) }
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                storedValue = value
                with (thisRef.preferences.edit()) {
                    putString(key, value?.let { adapter.toJson(value) })
                    apply()
                }
            }

        }
    }


}