package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import javax.inject.Singleton

@Module
object NetworkUtilsModule {

    @Provides
    @Singleton
    fun providesNetworkMonitor(context: Context): NetworkMonitor = NetworkMonitor(context)

}