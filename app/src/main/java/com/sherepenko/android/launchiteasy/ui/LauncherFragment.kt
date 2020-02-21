package com.sherepenko.android.launchiteasy.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.AppsAdapter
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.appsView
import kotlinx.android.synthetic.main.fragment_launcher.loadingView
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherFragment : BaseFragment(R.layout.fragment_launcher) {

    private val appsViewModel: AppsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appsAdapter = AppsAdapter()

        appsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = appsAdapter
        }

        appsViewModel.getInstalledApps().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        appsAdapter.items = data

                        if (data.isNotEmpty()) {
                            loadingView.visibility = View.GONE
                        }
                    }
                }
                Status.SUCCESS -> {
                    appsView.setItemViewCacheSize(it.data!!.size)
                    appsAdapter.items = it.data
                    loadingView.visibility = View.GONE
                }
                Status.ERROR -> {
                    loadingView.visibility = View.GONE
                }
            }
        })
    }
}
