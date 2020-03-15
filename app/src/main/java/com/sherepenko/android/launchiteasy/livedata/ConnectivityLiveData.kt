package com.sherepenko.android.launchiteasy.livedata

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectivityLiveData(
    context: Context
) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    init {
        @Suppress("DEPRECATION")
        postValue(connectivityManager.activeNetworkInfo?.isConnected ?: false)
    }

    override fun onActive() {
        super.onActive()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            }
            else -> {
                val networkRequest = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
