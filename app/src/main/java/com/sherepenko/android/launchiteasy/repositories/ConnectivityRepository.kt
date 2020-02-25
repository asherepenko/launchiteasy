package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.livedata.ConnectivityLiveData

abstract class ConnectivityRepository : BaseRepository {

    abstract fun getConnectionState(): LiveData<Boolean>
}

class ConnectivityRepositoryImpl(
    private val connectivityLiveData: ConnectivityLiveData
) : ConnectivityRepository() {

    override fun getConnectionState(): LiveData<Boolean> =
        connectivityLiveData
}
