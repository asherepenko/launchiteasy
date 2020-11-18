package com.sherepenko.android.launchiteasy.repositories

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.livedata.Event
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource
import java.time.Instant
import java.util.concurrent.TimeUnit

abstract class WeatherRepository : BaseRepository {

    abstract fun getCurrentWeather(): LiveData<Resource<WeatherItem>>

    abstract fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>>
}

class WeatherRepositoryImpl(
    private val connectivityRepository: ConnectivityRepository,
    private val locationRepository: LocationRepository,
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository() {

    companion object {
        private const val MAX_DISTANCE = 10000.0f // In metres

        private val WEATHER_MAX_STALE_TIME = TimeUnit.MINUTES.toMillis(30)

        private val FORECAST_MIN_STALE_TIME = TimeUnit.HOURS.toMillis(24)
    }

    private val currentWeatherUpdateEvent = MutableLiveData<Event<Boolean>>(Event(false))

    private val weatherForecastsUpdateEvent = MutableLiveData<Event<Boolean>>(Event(false))

    override fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        connectivityRepository.getConnectionState().switchMap { isConnected ->
            currentWeatherUpdateEvent.switchMap { event ->
                val forceUpdate = event.getContentIfNotHandled(false)!!
                locationRepository.getLastKnownLocation().switchMap { lastLocation ->
                    getCurrentWeather(isConnected, forceUpdate, lastLocation)
                }
            }
        }

    override fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        connectivityRepository.getConnectionState().switchMap { isConnected ->
            weatherForecastsUpdateEvent.switchMap { event ->
                val forceUpdate = event.getContentIfNotHandled(false)!!
                locationRepository.getLastKnownLocation().switchMap { lastLocation ->
                    getWeatherForecasts(isConnected, forceUpdate, lastLocation)
                }
            }
        }

    override fun forceUpdate() {
        currentWeatherUpdateEvent.postValue(Event(true))
        weatherForecastsUpdateEvent.postValue(Event(true))
    }

    private fun getCurrentWeather(
        isConnected: Boolean,
        forceUpdate: Boolean,
        lastLocation: LocationItem
    ): LiveData<Resource<WeatherItem>> =
        object : RemoteBoundResource<WeatherItem, WeatherItem>() {
            override suspend fun getRemoteData(): WeatherItem =
                remoteDataSource.getCurrentWeather(
                    lastLocation.latitude,
                    lastLocation.longitude
                )

            override suspend fun getLocalData(): WeatherItem =
                localDataSource.getCurrentWeather(
                    lastLocation.latitude,
                    lastLocation.longitude
                )

            override suspend fun saveLocally(data: WeatherItem) {
                localDataSource.saveCurrentWeather(data)
            }

            override suspend fun toLocalData(data: WeatherItem): WeatherItem =
                data

            override suspend fun shouldFetchRemoteData(data: WeatherItem?): Boolean =
                isConnected && (
                    forceUpdate || data?.let {
                        it.sinceLastUpdateMilli() > WEATHER_MAX_STALE_TIME ||
                            it.location.distanceTo(lastLocation) > MAX_DISTANCE
                    } ?: isConnected
                    )
        }.asLiveData()

    private fun getWeatherForecasts(
        isConnected: Boolean,
        forceUpdate: Boolean,
        lastLocation: LocationItem
    ): LiveData<Resource<List<ForecastItem>>> =
        object : RemoteBoundResource<List<ForecastItem>, List<ForecastItem>>() {
            override suspend fun getRemoteData(): List<ForecastItem> =
                remoteDataSource.getWeatherForecasts(
                    lastLocation.latitude,
                    lastLocation.longitude
                )

            override suspend fun getLocalData(): List<ForecastItem> =
                localDataSource.getWeatherForecasts(
                    lastLocation.latitude,
                    lastLocation.longitude
                )

            override suspend fun saveLocally(data: List<ForecastItem>) {
                localDataSource.saveWeatherForecasts(data)
            }

            override suspend fun toLocalData(data: List<ForecastItem>): List<ForecastItem> =
                data

            override suspend fun shouldFetchRemoteData(data: List<ForecastItem>?): Boolean =
                isConnected && (
                    forceUpdate || data?.let {
                        it.isEmpty() ||
                            it.last().tillNextUpdateMilli() < FORECAST_MIN_STALE_TIME ||
                            it.last().location.distanceTo(lastLocation) > MAX_DISTANCE
                    } ?: isConnected
                    )
        }.asLiveData()

    private fun WeatherItem.sinceLastUpdateMilli() =
        Instant.now().toEpochMilli() - timestamp.toEpochMilli()

    private fun ForecastItem.tillNextUpdateMilli() =
        timestamp.toEpochMilli() - Instant.now().toEpochMilli()

    private fun LocationItem.distanceTo(location: LocationItem): Float =
        FloatArray(1).apply {
            Location.distanceBetween(
                latitude,
                longitude,
                location.latitude,
                location.longitude,
                this
            )
        }[0]
}
