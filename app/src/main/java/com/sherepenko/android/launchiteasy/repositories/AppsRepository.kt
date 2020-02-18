package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.utils.RemoteBoundResource
import com.sherepenko.android.launchiteasy.data.utils.Resource
import com.sherepenko.android.launchiteasy.providers.AppsLocalDataSource
import com.sherepenko.android.launchiteasy.providers.AppsRemoteDataSource

interface AppsRepository {

    fun getInstalledApps(): LiveData<Resource<List<AppItem>>>

    fun updateInstalledApps()
}

class AppsRepositoryImpl(
    private val localDataSource: AppsLocalDataSource,
    private val remoteDataSource: AppsRemoteDataSource
) : AppsRepository {

    private val appsChanged = MutableLiveData<Boolean>(false)

    override fun getInstalledApps(): LiveData<Resource<List<AppItem>>> =
        appsChanged.switchMap {
            object : RemoteBoundResource<List<AppItem>, List<AppItem>>() {
                override suspend fun getRemoteData(): List<AppItem> =
                    remoteDataSource.getInstalledApps()

                override suspend fun getLocalData(): List<AppItem> =
                    localDataSource.getInstalledApps()

                override suspend fun saveLocally(data: List<AppItem>) {
                    localDataSource.saveInstalledApps(data)
                }

                override fun toLocalData(data: List<AppItem>): List<AppItem> =
                    data

                override fun shouldFetchRemoteData(data: List<AppItem>?): Boolean =
                    it || data.isNullOrEmpty()
            }.asLiveData()
        }

    override fun updateInstalledApps() {
        appsChanged.postValue(true)
    }
}
