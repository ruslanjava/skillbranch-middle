package ru.skillbranch.skillarticles.di.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.skillarticles.AppConfig
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.adapters.DateAdapter
import ru.skillbranch.skillarticles.data.remote.interceptors.ErrorStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.NetworkStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.TokenAuthenticator
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideTokenAuthenticator(prefs: PrefManager, laziApi: Lazy<RestService>): TokenAuthenticator {
        return TokenAuthenticator(prefs, laziApi)
    }

    @Singleton
    @Provides
    fun provideNetworkStatusInterceptor(networkMonitor: NetworkMonitor): NetworkStatusInterceptor {
        return NetworkStatusInterceptor(networkMonitor)
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() : HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideErrorStatusInterceptor(moshi: Moshi): ErrorStatusInterceptor {
        return ErrorStatusInterceptor(moshi)
    }

    @Singleton
    @Provides
    fun provideClient(
            tokenAuthenticator: TokenAuthenticator,
            networkStatusInterceptor: NetworkStatusInterceptor,
            loggingInterceptor: HttpLoggingInterceptor,
            errorStatusInterceptor: ErrorStatusInterceptor
    ): OkHttpClient {
        // client
        return OkHttpClient().newBuilder()
                .readTimeout(2, TimeUnit.SECONDS) // socket timeout (GET)
                .writeTimeout(5, TimeUnit.SECONDS) // socket timeout (POST, PUT, etc)
                .authenticator(tokenAuthenticator)
                .addInterceptor(networkStatusInterceptor) // intercept network status
                .addInterceptor(loggingInterceptor) // intercept req/res for logging
                .addInterceptor(errorStatusInterceptor) // intercept status errors
                .build()
    }

    @Singleton
    @Provides
    fun provideDateAdapter(): DateAdapter {
        return DateAdapter()
    }

    @Singleton
    @Provides
    fun provideMoshi(dateAdapter: DateAdapter): Moshi {
        return Moshi.Builder()
                .add(dateAdapter)
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi) : Retrofit {
        return Retrofit.Builder()
                .client(client) // set http client
                .addConverterFactory(MoshiConverterFactory.create(moshi)) // set json converter
                .baseUrl(AppConfig.BASE_URL)
                .build()
    }

    @Provides
    fun provideRestService(retrofit: Retrofit): RestService {
        return retrofit.create(RestService::class.java)
    }

}