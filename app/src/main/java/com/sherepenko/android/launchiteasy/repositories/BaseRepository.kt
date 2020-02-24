package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.MutableLiveData

abstract class BaseRepository {

    protected val forceUpdateChannel = MutableLiveData<Boolean>(false)

    fun forceUpdate() {
        forceUpdateChannel.postValue(true)
    }

    open fun dispose() {
    }
}
