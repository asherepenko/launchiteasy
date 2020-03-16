package com.sherepenko.android.launchiteasy.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.utils.isPermissionGranted
import kotlinx.coroutines.delay
import timber.log.Timber

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    companion object {
        private const val TAG = "Permissions"
        private const val REQUEST_RUNTIME_PERMISSIONS = 101
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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
                    Timber.tag(TAG).i("All requested permissions were GRANTED")
                    findNavController().navigateToHomeFragment()
                } else {
                    Timber.tag(TAG).e("Requested permissions are NOT GRANTED")
                    ActivityCompat.finishAffinity(requireActivity())
                }
            }
            else -> {
                Timber.tag(TAG).w("Unknown request code: $requestCode")
            }
        }
    }

    private fun checkPermissions() {
        val requestedPermissions = mutableListOf<String>()

        if (!requireActivity().isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Timber.tag(TAG).i("Request ${Manifest.permission.ACCESS_COARSE_LOCATION} permission")
            requestedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (requestedPermissions.isEmpty()) {
            findNavController().navigateToHomeFragment()
        } else {
            requestPermissions(
                requestedPermissions.toTypedArray(),
                REQUEST_RUNTIME_PERMISSIONS
            )
        }
    }

    private fun NavController.navigateToHomeFragment() {
        navigate(SplashFragmentDirections.toHomeFragment())
    }
}
