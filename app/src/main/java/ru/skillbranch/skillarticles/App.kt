package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class App : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        // TODO set default Night mode

        Stetho.initializeWithDefaults(this)
    }

    companion object {
        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

}