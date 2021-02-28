package ru.skillbranch.skillarticles.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.di.modules.NetworkUtilsModule
import ru.skillbranch.skillarticles.di.modules.PreferencesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class, NetworkUtilsModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(app: App)

    fun getNetworkManager(): NetworkManager
    fun getPrefManager(): PrefManager

    val activityComponent: ActivityComponent.Factory

}