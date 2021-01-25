package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.di.components.ActivityComponent
import ru.skillbranch.skillarticles.di.components.AppComponent
import ru.skillbranch.skillarticles.di.components.DaggerActivityComponent
import ru.skillbranch.skillarticles.di.components.DaggerAppComponent
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import ru.skillbranch.skillarticles.di.modules.NetworkUtilsModule
import ru.skillbranch.skillarticles.di.modules.PreferencesModule
import javax.inject.Inject

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

        appComponent = DaggerAppComponent.builder()
            .preferencesModule(PreferencesModule(applicationContext))
            .networkUtilsModule(NetworkUtilsModule(applicationContext))
            .build()

        appComponent.inject(this)

        // start network monitoring
        monitor.registerNetworkMonitor()

        // TODO set default Night mode
        val mode: Int = if (preferences.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {

        lateinit var activityComponent: ActivityComponent
        lateinit var appComponent: AppComponent

        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

    }

}