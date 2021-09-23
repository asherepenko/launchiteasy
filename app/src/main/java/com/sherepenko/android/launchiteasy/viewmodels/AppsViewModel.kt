package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.repositories.AppsRepository

class AppsViewModel(
    private val repository: AppsRepository
) : BaseViewModel<AppsRepository>(repository) {

    fun getInstalledApps(showSystemApps: Boolean = true): LiveData<Resource<List<AppItem>>> =
        repository.getInstalledApps(showSystemApps)
}
