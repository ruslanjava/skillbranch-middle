package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import javax.inject.Singleton

@Module
class NetworkUtilsModule(val context: Context) {

    @Provides
    @Singleton
    fun providesNetworkMonitor(): NetworkMonitor = NetworkMonitor(context)

}