package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.AppState
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData
import com.sherepenko.android.launchiteasy.livedata.Event

abstract class AppStateRepository : BaseRepository {

    abstract fun getAppState(): LiveData<Event<AppState>>
}

class AppStateRepositoryImpl(
    private val appStateLiveData: AppStateLiveData
) : AppStateRepository() {

    override fun getAppState(): LiveData<Event<AppState>> =
        appStateLiveData

    override fun forceUpdate() {
        appStateLiveData.postValue(Event(AppState.CHANGED))
    }
}
