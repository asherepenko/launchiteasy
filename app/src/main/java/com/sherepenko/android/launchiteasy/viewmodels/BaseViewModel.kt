package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.livedata.ConnectivityLiveData
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {

    private val connectivityLiveData: ConnectivityLiveData by inject()

    fun getConnectionState(): LiveData<Boolean> =
        connectivityLiveData
}
