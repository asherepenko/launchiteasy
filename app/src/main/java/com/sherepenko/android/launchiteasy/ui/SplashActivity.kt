package com.sherepenko.android.launchiteasy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sherepenko.android.launchiteasy.R
import kotlinx.coroutines.delay

class SplashActivity : BaseActivity(R.layout.activity_splash) {
    companion object {
        private const val REQUEST_RUNTIME_PERMISSIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            delay(1000)
            checkPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_RUNTIME_PERMISSIONS -> {
                var permissionsGranted = permissions.size == grantResults.size

                if (permissionsGranted) {
                    for (grantResult in grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false
                            break
                        }
                    }
                }

                if (permissionsGranted) {
                    startActivity(MainActivity.homeIntent(this@SplashActivity))
                    finish()
                } else {
                    finishAffinity()
                }
            }
            else -> {
                // ignore
            }
        }
    }

    private fun checkPermissions() {
        val requestedPermissions = mutableListOf<String>()

        if (!Manifest.permission.ACCESS_COARSE_LOCATION.isGranted()) {
            requestedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (requestedPermissions.isEmpty()) {
            startActivity(MainActivity.homeIntent(this@SplashActivity))
            finish()
        } else {
            ActivityCompat.requestPermissions(
                this@SplashActivity,
                requestedPermissions.toTypedArray(),
                REQUEST_RUNTIME_PERMISSIONS
            )
        }
    }

    private fun String.isGranted(): Boolean =
        ContextCompat.checkSelfPermission(this@SplashActivity, this) ==
                PackageManager.PERMISSION_GRANTED
}
