package com.sherepenko.android.launchiteasy.livedata

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sherepenko.android.launchiteasy.data.LocationItem
import java.util.concurrent.TimeUnit

class LocationLiveData(
    context: Context
) : LiveData<LocationItem>() {

    private val locationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.forEach {
                postValue(LocationItem(it.latitude, it.longitude))
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.HOURS.toMillis(1)
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    init {
        locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                postValue(LocationItem(it.latitude, it.longitude))
            }
        }
    }

    override fun onActive() {
        super.onActive()
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}
