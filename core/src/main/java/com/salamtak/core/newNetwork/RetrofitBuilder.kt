package com.salamtak.core.newNetwork

import android.util.Log
import com.salamtak.core.BuildConfig
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.network.ApiEndpoints
import com.salamtak.core.usecase.UseCaseModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class RetrofitBuilder {

    val instant: ApiEndpoints by lazy {
        val okHttpClient = getOkHttpClient()
        val retrofit = Retrofit.Builder().baseUrl(UseCaseModule.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(okHttpClient)
            .build()
        //create retrofit client
        return@lazy retrofit.create(ApiEndpoints::class.java)
    }

    private val logging: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun getOkHttpClient(): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {

            okHttpClientBuilder.addInterceptor{
                chain ->
                var builders = chain.request()
                try {
                    builders = chain.request().newBuilder().build()
                }catch (exception : Exception){exception.printStackTrace()}


                chain.proceed(builders)
            }.addInterceptor(logging)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
//            okHttpClientBuilder
//                .addInterceptor(ChuckInterceptor(UserData.context))
//                .addInterceptor(logging)

        }

        okHttpClientBuilder.addInterceptor { chain ->

            try {
                val originalRequest = chain.request()
                val requestBuilder =
                    originalRequest.newBuilder()

                UserData.getToken()
                    ?.let {
                        if (it.isNotEmpty())
                            requestBuilder.addHeader("Authorization", "Bearer $it")
                        Log.d("token", "getOkHttpClient: $it")
                    }
//                UserData.getLanguageID().let {
//                    requestBuilder.addHeader("Accept-Language", Localization.getLanguageName())
//                }
//                requestBuilder.addHeader("Authorization","token 598e4c61f1a98c62d2042552ab30752520280f73") // for testing
                chain.proceed(requestBuilder.build())
            } catch (exception: SocketTimeoutException) {
                exception.printStackTrace()
                chain.proceed(chain.request())
            }

        }

        return okHttpClientBuilder.build()
    }

}