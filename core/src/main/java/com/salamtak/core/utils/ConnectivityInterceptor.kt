package com.salamtak.core.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.salamtak.domain.entities.NoInternetException
import okhttp3.Interceptor
import okhttp3.Response

import java.lang.ref.WeakReference

class ConnectivityInterceptor(private val ctxWeakRef: WeakReference<Context>) : Interceptor {

    @Volatile
    private var isConnected: Boolean? = null
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    init {
        val cm: ConnectivityManager? = ctxWeakRef.get()?.getSystemService()
        val callback =
        object : ConnectivityManager.NetworkCallback() {
            
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected = false
            }
        }
        cm?.registerNetworkCallback(networkRequest, callback)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        require(ctxWeakRef.get() is Application) { "Use Application context to avoid memory leaks" }
        return synchronized(ConnectivityInterceptor::class) {
            isConnected.takeIf { it == true }?.let {
                return@let chain.request().run(chain::proceed)
            } ?: throw NoInternetException()
        }

    }
}