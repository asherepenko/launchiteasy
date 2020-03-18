package com.sherepenko.android.launchiteasy.livedata

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sherepenko.android.launchiteasy.BuildConfig
import com.sherepenko.android.launchiteasy.data.LocationItem
import java.util.concurrent.TimeUnit
import timber.log.Timber

class LocationLiveData(
    context: Context
) : LiveData<LocationItem>() {

    companion object {
        private const val TAG = "Location"

        private val FALLBACK_LOCATION = LocationItem(50.4501, 30.5234)
    }

    private val locationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.forEach {
                Timber.tag(TAG).i("Current location updated: $it")
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

    override fun onActive() {
        super.onActive()
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}
