package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.repositories.ConnectivityRepository

class ConnectivityViewModel(
    private val repository: ConnectivityRepository
) : BaseViewModel<ConnectivityRepository>(repository) {

    fun getConnectionState(): LiveData<Boolean> =
        repository.getConnectionState()
}
