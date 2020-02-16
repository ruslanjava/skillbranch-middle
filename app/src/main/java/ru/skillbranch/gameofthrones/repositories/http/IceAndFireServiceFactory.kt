package ru.skillbranch.gameofthrones.repositories.http

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.gameofthrones.AppConfig
import java.util.concurrent.TimeUnit

internal object IceAndFireServiceFactory {

    val instance: IceAndFireService by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .client(provideOkhttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(AppConfig.BASE_URL)
            .build()

        retrofit.create(IceAndFireService::class.java)
    }

    private fun provideOkhttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)

        val loggingInterceptor = provideLoggingInterceptor();
        client.addInterceptor(loggingInterceptor)
        return client.build()
    }

    private fun provideLoggingInterceptor() : HttpLoggingInterceptor {
        val logger = object: HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                println(message)
            }
        }
        val interceptor = HttpLoggingInterceptor(logger)
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}


