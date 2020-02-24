package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData

abstract class AppStateRepository : BaseRepository() {

    abstract fun getAppState(): LiveData<Boolean>
}

class AppStateRepositoryImpl(
    private val appStateLiveData: AppStateLiveData
) : AppStateRepository() {

    override fun getAppState(): LiveData<Boolean> =
        appStateLiveData
}
