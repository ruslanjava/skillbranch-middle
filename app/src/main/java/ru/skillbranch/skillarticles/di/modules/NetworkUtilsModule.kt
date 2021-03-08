package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.interceptors.TokenAuthenticator
import javax.inject.Singleton

@Module
object NetworkUtilsModule {

    @Provides
    @Singleton
    fun providesNetworkMonitor(context: Context): NetworkMonitor = NetworkMonitor(context)

}
