package com.sherepenko.android.launchiteasy.repositories

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.livedata.ConnectivityLiveData
import com.sherepenko.android.launchiteasy.livedata.LocationLiveData
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource
import java.util.concurrent.TimeUnit
import org.threeten.bp.Instant

interface WeatherRepository : BaseRepository {

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>>

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>>
}

class WeatherRepositoryImpl(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource,
    private val locationDataSource: LocationLiveData,
    private val connectivityDataSource: ConnectivityLiveData
) : WeatherRepository {

    companion object {
        private const val MAX_DISTANCE = 1500.0f // In metres

        private val WEATHER_MAX_STALE_TIME = TimeUnit.HOURS.toMillis(2)

        private val FORECAST_MIN_STALE_TIME = TimeUnit.HOURS.toMillis(27)
    }

    override fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        connectivityDataSource.switchMap { isConnected ->
            locationDataSource.switchMap { lastLocation ->
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
                            it.sinceLastUpdateMilli() > WEATHER_MAX_STALE_TIME ||
                                it.location.distanceTo(lastLocation) > MAX_DISTANCE
                        } ?: isConnected
                }.asLiveData()
            }
        }

    override fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        connectivityDataSource.switchMap { isConnected ->
            locationDataSource.switchMap { lastLocation ->
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

                    override fun toLocalData(data: List<ForecastItem>): List<ForecastItem> =
                        data

                    override fun shouldFetchRemoteData(data: List<ForecastItem>?): Boolean =
                        data?.let {
                            it.isEmpty() ||
                                it.last().tillNextUpdateMilli() < FORECAST_MIN_STALE_TIME ||
                                    it.last().location.distanceTo(lastLocation) > MAX_DISTANCE
                        } ?: isConnected
                }.asLiveData()
            }
        }

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
