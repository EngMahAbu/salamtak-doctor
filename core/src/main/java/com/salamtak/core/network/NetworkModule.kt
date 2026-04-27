package com.salamtak.core.network

import android.content.Context
import com.salamtak.core.usecase.UseCaseModule.BASE_URL
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

object NetworkModule :
        (Context, String, Boolean,ArrayList<Interceptor>) -> Module {
    const val HEADERS_INTERCEPTOR = "HEADERS_Intercept"

    const val LOGGING_INTERCEPTOR = "Logging_Intercept"
    private const val OK_HTTP = "OK_HTTP_CLIENT"

    const val CONTENT_RETROFIT_CLIENT = "CONTENT_RETRO_CLIENT_CLIENT"

    override fun invoke(
        context: Context,
        contentBaseUrl: String,
        isDebugging: Boolean,
        interceptors: ArrayList<Interceptor>
    ) = module {

        single(StringQualifier(LOGGING_INTERCEPTOR)) {
            HttpLoggingInterceptor().apply {
                HttpLoggingInterceptor.Level.BODY
            }
        }

        single(StringQualifier(HEADERS_INTERCEPTOR)) {
            HeadersInterceptor(context)
        }


        single(StringQualifier(OK_HTTP)) {
            val interceptorsArray = arrayListOf(
                get<HeadersInterceptor>(StringQualifier(HEADERS_INTERCEPTOR))
            )
            NetworkClients.createOkHttpClient(interceptorsArray)
        }

        single(createdAtStart = true) {
            createWebClient(
                get(StringQualifier(CONTENT_RETROFIT_CLIENT))
            ).value
        } bind WebClient::class as KClass<WebClientCreator>


        // retrofit Client for Content (news..etc)
        single<ApiEndpoints>(StringQualifier(CONTENT_RETROFIT_CLIENT)) {
            NetworkClients.createRetroFitBuilder(
                contentBaseUrl,
                get(StringQualifier(OK_HTTP))
            ).build().create(ApiEndpoints::class.java)
        }

    }



}

