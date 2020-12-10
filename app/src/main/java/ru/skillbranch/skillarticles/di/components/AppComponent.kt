package ru.skillbranch.skillarticles.di.components

import dagger.Component
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.di.modules.NetworkUtilsModule
import ru.skillbranch.skillarticles.di.modules.PreferencesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class, NetworkUtilsModule::class])
interface AppComponent {

    fun inject(app: App)
    fun getPreferences(): PrefManager
    fun getNetworkMonitor(): NetworkMonitor

}