package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.di.components.AppComponent
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var monitor: NetworkMonitor
    @Inject
    lateinit var preferences: PrefManager

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        // start network monitoring
        monitor.registerNetworkMonitor()

        //set saved night/day mode
        val mode: Int = if (preferences.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {

        lateinit var appComponent: AppComponent

        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

    }

}