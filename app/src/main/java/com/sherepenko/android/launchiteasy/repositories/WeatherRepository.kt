package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.utils.RemoteBoundResource
import com.sherepenko.android.launchiteasy.data.utils.Resource
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource

interface WeatherRepository {

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>>

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>>

    fun updateCurrentLocation(locationId: Int)
}

class WeatherRepositoryImpl(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    private val currentLocation = MutableLiveData(703448)

    override fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        currentLocation.switchMap {
            object : RemoteBoundResource<WeatherItem, WeatherItem>() {
                override suspend fun getRemoteData(): WeatherItem =
                    remoteDataSource.getCurrentWeather(it)

                override suspend fun getLocalData(): WeatherItem =
                    localDataSource.getCurrentWeather(it)

                override suspend fun saveLocally(data: WeatherItem) {
                    localDataSource.saveCurrentWeather(data)
                }

                override fun toLocalData(data: WeatherItem): WeatherItem =
                    data

                override fun shouldFetchRemoteData(data: WeatherItem?): Boolean =
                    true
            }.asLiveData()
        }

    override fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        currentLocation.switchMap {
            object : RemoteBoundResource<List<ForecastItem>, List<ForecastItem>>() {
                override suspend fun getRemoteData(): List<ForecastItem> =
                    remoteDataSource.getWeatherForecasts(it)

                override suspend fun getLocalData(): List<ForecastItem> =
                    localDataSource.getWeatherForecasts(it)

                override suspend fun saveLocally(data: List<ForecastItem>) {
                    localDataSource.saveWeatherForecasts(data)
                }

                override fun toLocalData(data: List<ForecastItem>): List<ForecastItem> =
                    data

                override fun shouldFetchRemoteData(data: List<ForecastItem>?): Boolean =
                    true
            }.asLiveData()
        }

    override fun updateCurrentLocation(locationId: Int) {
        currentLocation.postValue(locationId)
    }
}
