package com.salamtak.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


internal object NetworkClients {

    fun createOkHttpClient(interceptors: List<Interceptor>): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    fun createRetroFitBuilder(baserUrl:String,client: OkHttpClient): Retrofit.Builder {
        val builder = Retrofit.Builder()
        builder.baseUrl(baserUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
        return builder
    }
}