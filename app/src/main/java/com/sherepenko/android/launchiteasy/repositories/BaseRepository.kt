package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.MutableLiveData

abstract class BaseRepository {

    protected val updateChannel = MutableLiveData<Boolean>(true)

    open fun dispose() {
    }

    fun forceUpdate() {
        updateChannel.postValue(true)
    }
}
