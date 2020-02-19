package com.sherepenko.android.launchiteasy.repositories

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.utils.RemoteBoundResource
import com.sherepenko.android.launchiteasy.data.utils.Resource
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource
import com.sherepenko.android.launchiteasy.utils.round
import java.util.concurrent.TimeUnit
import org.threeten.bp.Instant

interface WeatherRepository : BaseRepository {

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>>

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>>

    fun updateCurrentLocation(latitude: Double, longitude: Double)
}

class WeatherRepositoryImpl(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    companion object {
        private const val MAX_DISTANCE = 1500.0f
        private val MAX_STALE_TIME = TimeUnit.HOURS.toMillis(2)
    }

    private val currentLocation = MutableLiveData<LocationItem>()

    override fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        currentLocation.switchMap { lastLocation ->
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

                override fun toLocalData(data: WeatherItem): WeatherItem =
                    data

                override fun shouldFetchRemoteData(data: WeatherItem?): Boolean =
                    data?.let {
                        val sinceLastUpdate =
                            Instant.now().toEpochMilli() - it.timestamp.toEpochMilli()

                        val distances = FloatArray(3)

                        Location.distanceBetween(
                            it.location.latitude,
                            it.location.longitude,
                            lastLocation.latitude,
                            lastLocation.longitude,
                            distances
                        )

                        sinceLastUpdate > MAX_STALE_TIME || distances[0] > MAX_DISTANCE
                    } ?: true
            }.asLiveData()
        }

    override fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        currentLocation.switchMap {
            object : RemoteBoundResource<List<ForecastItem>, List<ForecastItem>>() {
                override suspend fun getRemoteData(): List<ForecastItem> =
                    remoteDataSource.getWeatherForecasts(it.latitude, it.longitude)

                override suspend fun getLocalData(): List<ForecastItem> =
                    localDataSource.getWeatherForecasts(it.latitude, it.longitude)

                override suspend fun saveLocally(data: List<ForecastItem>) {
                    localDataSource.saveWeatherForecasts(data)
                }

                override fun toLocalData(data: List<ForecastItem>): List<ForecastItem> =
                    data

                override fun shouldFetchRemoteData(data: List<ForecastItem>?): Boolean =
                    true
            }.asLiveData()
        }

    override fun updateCurrentLocation(latitude: Double, longitude: Double) {
        currentLocation.postValue(
            LocationItem(
                latitude = latitude.round(),
                longitude = longitude.round()
            )
        )
    }
}
