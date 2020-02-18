package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.utils.Resource
import com.sherepenko.android.launchiteasy.repositories.AppsRepository

class AppsViewModel(
    private val repository: AppsRepository
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        repository.dispose()
    }

    fun getInstalledApps(): LiveData<Resource<List<AppItem>>> =
        repository.getInstalledApps()

    fun updateInstallApps() {
        repository.updateInstalledApps()
    }
}
