package com.sherepenko.android.launchiteasy.livedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData

class AppStateLiveData(
    private val context: Context
) : LiveData<Boolean>() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_CHANGED,
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_REPLACED -> {
                    postValue(true)
                }
                else -> {
                    // ignore
                }
            }
        }
    }

    private val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
    }

    init {
        postValue(false)
    }

    override fun onActive() {
        super.onActive()
        context.registerReceiver(receiver, intentFilter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(receiver)
    }
}
