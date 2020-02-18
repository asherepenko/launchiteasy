package com.sherepenko.android.launchiteasy.providers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.sherepenko.android.launchiteasy.data.db.AppDatabase
import com.sherepenko.android.launchiteasy.BuildConfig
import com.sherepenko.android.launchiteasy.data.AppItem
import java.util.*

interface AppsDataSource {

    suspend fun getInstalledApps(): List<AppItem>
}

class AppsLocalDataSource(database: AppDatabase) :
    AppsDataSource {

    private val appDao = database.getAppDao()

    override suspend fun getInstalledApps(): List<AppItem> =
        appDao.getAllApps()

    suspend fun saveInstalledApps(apps: List<AppItem>) =
        appDao.insertApps(*apps.toTypedArray())
}

class AppsRemoteDataSource(context: Context) :
    AppsDataSource {

    private val packageManager = context.packageManager

    override suspend fun getInstalledApps(): List<AppItem> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .asSequence()
            .filter {
                packageManager.getLaunchIntentForPackage(it.packageName) != null
            }
            .filter {
                it.packageName != BuildConfig.APPLICATION_ID
            }
            .map {
                AppItem(
                    it.packageName,
                    packageManager.getApplicationLabel(it).toString(),
                    it.isSystem(),
                    it.isEnabled()
                )
            }
            .sortedBy {
                it.label.toLowerCase(Locale.getDefault())
            }
            .toList()

    private fun ApplicationInfo.isSystem(): Boolean =
        (this.flags and ApplicationInfo.FLAG_SYSTEM) != 0

    private fun ApplicationInfo.isEnabled(): Boolean {
        val enabled = packageManager.getApplicationEnabledSetting(this.packageName)
        return enabled == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
            enabled == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }
}
