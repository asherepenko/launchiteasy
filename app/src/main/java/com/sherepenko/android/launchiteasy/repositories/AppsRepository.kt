package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData
import com.sherepenko.android.launchiteasy.providers.AppsLocalDataSource
import com.sherepenko.android.launchiteasy.providers.AppsRemoteDataSource

interface AppsRepository : BaseRepository {

    fun getInstalledApps(): LiveData<Resource<List<AppItem>>>
}

class AppsRepositoryImpl(
    private val localDataSource: AppsLocalDataSource,
    private val remoteDataSource: AppsRemoteDataSource,
    private val appStateDataSource: AppStateLiveData
) : AppsRepository {

    override fun getInstalledApps(): LiveData<Resource<List<AppItem>>> =
        appStateDataSource.switchMap { appsChanged ->
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
                    appsChanged || data.isNullOrEmpty()
            }.asLiveData()
        }
}
