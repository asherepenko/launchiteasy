package com.sherepenko.android.launchiteasy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LaunchActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_RUNTIME_PERMISSIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
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
                    startActivity(MainActivity.homeIntent(this@LaunchActivity))
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
            startActivity(MainActivity.homeIntent(this@LaunchActivity))
            finish()
        } else {
            ActivityCompat.requestPermissions(
                this@LaunchActivity,
                requestedPermissions.toTypedArray(),
                REQUEST_RUNTIME_PERMISSIONS
            )
        }
    }

    private fun String.isGranted(): Boolean =
        ContextCompat.checkSelfPermission(this@LaunchActivity, this) ==
                PackageManager.PERMISSION_GRANTED
}
