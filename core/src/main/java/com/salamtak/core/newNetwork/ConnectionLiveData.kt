package com.ebling.goldenrose.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionLiveData(context: Context): LiveData<Boolean>() {


    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var cm =  context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetwork:MutableSet<Network> = HashSet()

    fun checkValidNetworks() {
        postValue(validNetwork.size > 0)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder().addCapability(NET_CAPABILITY_INTERNET).build()
        cm.requestNetwork(networkRequest, networkCallback)

    }

    override fun onInactive() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object :ConnectivityManager.NetworkCallback () {
        override fun onAvailable(network: Network) {
            val networkCapabilities = cm.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true){
                CoroutineScope(Dispatchers.IO).launch{
                    // ping to google to check if there is internet
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet){
                        withContext(Dispatchers.Main){
                            validNetwork.add(network)
                            checkValidNetworks()
                        }

                    }
                }

            }
        }

        override fun onLost(network: Network) {
            validNetwork.remove(network)
            checkValidNetworks()
        }
    }
}