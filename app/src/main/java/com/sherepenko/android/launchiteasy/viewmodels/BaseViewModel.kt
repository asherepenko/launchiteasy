package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.repositories.BaseRepository
import org.koin.core.component.KoinComponent

abstract class BaseViewModel<R : BaseRepository>(
    private vararg val repositories: R
) : ViewModel(), KoinComponent {

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
