package com.sherepenko.android.launchiteasy.livedata

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sherepenko.android.launchiteasy.BuildConfig
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.utils.isPermissionGranted
import java.util.concurrent.TimeUnit
import timber.log.Timber

@SuppressLint("MissingPermission")
class LocationLiveData(
    private val context: Context
) : LiveData<LocationItem>() {

    companion object {
        private const val TAG = "Location"

        private val FALLBACK_LOCATION = LocationItem(50.4501, 30.5234)
    }

    private val locationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach {
                Timber.tag(TAG).i("Current location updated: $it")
                postValue(LocationItem(it.latitude, it.longitude))
            }
        }
    }

    private val locationRequest = LocationRequest.Builder(TimeUnit.HOURS.toMillis(1))
        .build()

    init {
        if (context.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Timber.tag(TAG).i("Last known location: $location")
                    postValue(LocationItem(location.latitude, location.longitude))
                } else if (BuildConfig.DEBUG) {
                    postValue(FALLBACK_LOCATION)
                }
            }

            locationProviderClient.lastLocation.addOnFailureListener {
                Timber.tag(TAG).e(it, "Unable to get last known location")
            }
        }
    }

    override fun onActive() {
        super.onActive()
        if (context.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    override fun onInactive() {
        super.onInactive()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}
