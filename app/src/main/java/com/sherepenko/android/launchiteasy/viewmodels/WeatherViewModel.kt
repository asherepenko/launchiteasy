package com.sherepenko.android.launchiteasy.viewmodels

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.repositories.LocationRepository
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository
import java.io.IOException
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class WeatherViewModel(
    private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : BaseViewModel<WeatherRepository>(weatherRepository) {

    companion object {
        private const val TAG = "WeatherViewModel"
        private const val UNKNOWN_LOCATION = "Unknown"
    }

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        weatherRepository.getCurrentWeather()

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        weatherRepository.getWeatherForecasts()

    fun getCurrentLocationName(): LiveData<Resource<String>> =
        locationRepository.getLastKnownLocation().switchMap { lastLocation ->
            liveData(Dispatchers.IO) {
                emit(Resource.loading())
                val geocoder = Geocoder(context, Locale.ENGLISH)

                try {
                    val addresses = geocoder.getFromLocation(
                        lastLocation.latitude,
                        lastLocation.longitude,
                        1
                    )

                    if (addresses.isNullOrEmpty()) {
                        emit(
                            Resource.error<String>()
                        )
                    } else {
                        val locationName = addresses[0].locality

                        if (locationName.isNullOrEmpty()) {
                            emit(
                                Resource.error<String>()
                            )
                        } else {
                            emit(
                                Resource.success(
                                    locationName
                                )
                            )
                        }
                    }
                } catch (e: IOException) {
                    emit(
                        Resource.error(e, UNKNOWN_LOCATION)
                    )

                    Timber.tag(TAG).e(e, "Unable to parse current location")
                }
            }
        }
}
