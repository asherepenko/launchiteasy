package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.AppState
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData
import com.sherepenko.android.launchiteasy.livedata.LiveEvent

abstract class AppStateRepository : BaseRepository {

    abstract fun getAppState(): LiveData<LiveEvent<AppState>>
}

class AppStateRepositoryImpl(
    private val appStateLiveData: AppStateLiveData
) : AppStateRepository() {

    override fun getAppState(): LiveData<LiveEvent<AppState>> =
        appStateLiveData

    override fun forceUpdate() {
        appStateLiveData.postValue(LiveEvent(AppState.UPDATED))
    }
}
