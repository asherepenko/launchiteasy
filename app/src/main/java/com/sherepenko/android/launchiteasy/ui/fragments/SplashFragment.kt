package com.sherepenko.android.launchiteasy.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Timber.tag(TAG).i("All requested permissions were GRANTED")
                findNavController().navigateToHomeFragment()
            } else {
                Timber.tag(TAG).e("Requested permissions are NOT GRANTED")
                ActivityCompat.finishAffinity(requireActivity())
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(1000)
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (requireActivity().isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            findNavController().navigateToHomeFragment()
        } else {
            Timber.tag(TAG).i("Request ${Manifest.permission.ACCESS_COARSE_LOCATION} permission")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun NavController.navigateToHomeFragment() {
        navigate(SplashFragmentDirections.toHomeFragment())
    }
}
