package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.livedata.LocationLiveData

abstract class LocationRepository : BaseRepository() {

    abstract fun getLastKnownLocation(): LiveData<LocationItem>
}

class LocationRepositoryImpl(
    private val locationLiveData: LocationLiveData
) : LocationRepository() {

    override fun getLastKnownLocation(): LiveData<LocationItem> =
        locationLiveData
}
