package ru.skillbranch.skillarticles.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import ru.skillbranch.skillarticles.di.modules.NetworkModule
import ru.skillbranch.skillarticles.di.modules.NetworkUtilsModule
import ru.skillbranch.skillarticles.di.modules.PreferencesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class,
    NetworkUtilsModule::class, NetworkModule::class,
    ActivityModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    // fun getRestService(): RestService
    // fun getPrefManager(): PrefManager

}