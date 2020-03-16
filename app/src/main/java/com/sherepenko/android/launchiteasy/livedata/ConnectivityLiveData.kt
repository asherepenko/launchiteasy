package com.sherepenko.android.launchiteasy.livedata

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import timber.log.Timber

class ConnectivityLiveData(
    context: Context
) : LiveData<Boolean>() {

    companion object {
        private const val TAG = "Connectivity"
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Timber.tag(TAG).i("Network connection is available")
            postValue(true)
        }

        override fun onLost(network: Network) {
            Timber.tag(TAG).i("Network connection has been lost")
            postValue(false)
        }
    }

    init {
        @Suppress("DEPRECATION")
        val isConnected = connectivityManager.activeNetworkInfo?.isConnected ?: false
        Timber.tag(TAG).i("Current network status: isConnected=$isConnected")
        postValue(isConnected)
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
