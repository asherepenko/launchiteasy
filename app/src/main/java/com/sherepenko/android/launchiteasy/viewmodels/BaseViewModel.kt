package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.repositories.BaseRepository
import org.koin.core.KoinComponent

abstract class BaseViewModel<R : BaseRepository>(
    protected val repository: R
) : ViewModel(), KoinComponent {

    override fun onCleared() {
        super.onCleared()
        repository.dispose()
    }

    open fun forceUpdate() {
        repository.forceUpdate()
    }
}
