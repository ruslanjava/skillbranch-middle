package ru.skillbranch.gameofthrones.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.skillbranch.gameofthrones.AppConfig
import java.util.concurrent.TimeUnit

internal object IceAndFireServiceFactory {

    fun newInstance() : IceAndFireService {
        val retrofit = retrofit2.Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(AppConfig.BASE_URL)
            .client(provideOkhttpClient())
            .build()
        return retrofit.create(IceAndFireService::class.java)
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


