package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.repositories.BaseRepository

abstract class BaseViewModel<R : BaseRepository>(
    private vararg val repositories: R
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        repositories.forEach {
            it.dispose()
        }
    }

    open fun forceUpdate() {
        repositories.forEach {
            it.forceUpdate()
        }
    }
}
