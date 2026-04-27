package com.salamtak.core.network

 import android.content.Context
 import com.salamtak.core.utils.SessionManagement
 import okhttp3.Interceptor
 import okhttp3.Request
 import okhttp3.Response
 import org.koin.core.component.KoinComponent
 import org.koin.core.component.inject


class HeadersInterceptor(context: Context) : Interceptor , KoinComponent {

    companion object {
        private const val LANG_CODE = "Accept-Language"
        private const val AUTHORIZATION = "Authorization"
     }

      private val sessionManagement: SessionManagement by  inject()


    /*var interceptor = HttpLoggingInterceptor()


    var okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(
        Interceptor { chain ->
            val original = chain.request()
            // Request customization: add request headers
            val requestBuilder = original.newBuilder().method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        })
        .addInterceptor(interceptor).connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS).build()*/


    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        //TODO chane later
        val newRequest: Request = request.newBuilder().method(request.method, request.body)
            .header("Content-Type","application/json")
//            .header("accept","text/plain")
//            .header("Connection", "close")
//            .header("Accept-Encoding", "identity")
//            .header(AUTHORIZATION,"Bearer ${sessionManagement.getValue(KEY_USER_TOKEN) ?: ""}")
            .build()
        newRequest.url.newBuilder()
            .addQueryParameter("locale",  "en")
        return chain.proceed(newRequest)
    }
}