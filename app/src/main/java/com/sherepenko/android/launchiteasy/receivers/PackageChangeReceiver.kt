package com.sherepenko.android.launchiteasy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sherepenko.android.launchiteasy.repositories.AppsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class PackageChangeReceiver : BroadcastReceiver(), KoinComponent {

    private val appsRepository: AppsRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> {

            }
            Intent.ACTION_PACKAGE_ADDED -> {
                appsRepository.updateInstalledApps()
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                appsRepository.updateInstalledApps()
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                appsRepository.updateInstalledApps()
            }
            Intent.ACTION_PACKAGE_CHANGED -> {
                appsRepository.updateInstalledApps()
            }
            else -> {
                // ignore
            }
        }
    }
}
