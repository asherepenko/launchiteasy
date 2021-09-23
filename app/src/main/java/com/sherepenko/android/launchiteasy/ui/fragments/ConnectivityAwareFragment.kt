package com.sherepenko.android.launchiteasy.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.snackbar.Snackbar
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.viewmodels.ConnectivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class ConnectivityAwareFragment(
    @LayoutRes contentLayoutId: Int
) : BaseFragment(contentLayoutId) {

    private val connectivityViewModel: ConnectivityViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbar = Snackbar.make(
            view,
            R.string.no_connection,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.dismiss) {
                hideSnackbar()
            }
        }

        connectivityViewModel.getConnectionState().observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                snackbar.showSnackbar()
            } else {
                snackbar.hideSnackbar()
            }
        }
    }
}

private fun Snackbar.showSnackbar() {
    if (!isShownOrQueued) {
        show()
    }
}

private fun Snackbar.hideSnackbar() {
    if (isShownOrQueued) {
        dismiss()
    }
}
