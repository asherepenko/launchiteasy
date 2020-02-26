package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.AppState
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.providers.AppsLocalDataSource
import com.sherepenko.android.launchiteasy.providers.AppsRemoteDataSource

abstract class AppsRepository : BaseRepository {

    abstract fun getInstalledApps(showSystemApps: Boolean): LiveData<Resource<List<AppItem>>>
}

class AppsRepositoryImpl(
    private val appStateRepository: AppStateRepository,
    private val localDataSource: AppsLocalDataSource,
    private val remoteDataSource: AppsRemoteDataSource
) : AppsRepository() {

    override fun getInstalledApps(showSystemApps: Boolean): LiveData<Resource<List<AppItem>>> =
        appStateRepository.getAppState().switchMap { event ->
            val appState = event.getContentIfNotHandled(AppState.INITIAL)!!
            getInstalledApps(showSystemApps, appState)
        }

    private fun getInstalledApps(
        showSystemApps: Boolean,
        appState: AppState
    ): LiveData<Resource<List<AppItem>>> =
        object : RemoteBoundResource<List<AppItem>, List<AppItem>>() {
            override suspend fun getRemoteData(): List<AppItem> =
                remoteDataSource.getInstalledApps()

            override suspend fun getLocalData(): List<AppItem> =
                localDataSource.getInstalledApps().filter {
                    showSystemApps || !it.isSystem
                }

            override suspend fun saveLocally(data: List<AppItem>) {
                localDataSource.saveInstalledApps(data)
            }

            override fun toLocalData(data: List<AppItem>): List<AppItem> =
                data

            override fun shouldFetchRemoteData(data: List<AppItem>?): Boolean =
                appState != AppState.INITIAL || data.isNullOrEmpty()
        }.asLiveData()
}
