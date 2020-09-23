package com.sherepenko.android.launchiteasy.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.AppsAdapter
import com.sherepenko.android.launchiteasy.ui.adapters.OnItemClickListener
import com.sherepenko.android.launchiteasy.utils.PreferenceHelper
import com.sherepenko.android.launchiteasy.utils.launchActivity
import com.sherepenko.android.launchiteasy.utils.launchActivityIfResolved
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.appsView
import kotlinx.android.synthetic.main.fragment_launcher.loadingView
import kotlinx.android.synthetic.main.fragment_launcher.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.inject
import timber.log.Timber

class LauncherFragment : BaseFragment(R.layout.fragment_launcher) {

    companion object {
        private const val TAG = "LauncherFragment"
    }

    private val appsViewModel: AppsViewModel by viewModel()

    private val prefs: PreferenceHelper by inject()

    private lateinit var appsAdapter: AppsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupInstalledApps()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.actionSettings -> {
                findNavController().navigateToSettingsFragment()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarView)
        }
    }

    private fun setupInstalledApps() {
        appsAdapter = AppsAdapter()

        appsAdapter.itemClickListener = object :
            OnItemClickListener {
            override fun onItemClick(view: View, position: Int, id: Long) {
                val packageName = appsAdapter.getPackageName(position)
                Timber.tag(TAG).i("Launching package: $packageName")

                requireActivity().packageManager.getLaunchIntentForPackage(packageName)?.let {
                    requireActivity().launchActivity(
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }

            override fun onItemLongClick(view: View, position: Int, id: Long) {
                val packageName = appsAdapter.getPackageName(position)

                PopupMenu(requireActivity(), view, Gravity.TOP).apply {
                    inflate(R.menu.app_details_menu)

                    Timber.tag(TAG).d("Show details menu for: $packageName")

                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.actionAppInfo -> {
                                Timber.tag(TAG).i("Info action called for: $packageName")
                                requireActivity().launchActivityIfResolved(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse("package:$packageName"))
                                )
                                true
                            }
                            R.id.actionAppUninstall -> {
                                Timber.tag(TAG).i("Delete action called for: $packageName")
                                requireActivity().launchActivityIfResolved(
                                    Intent(Intent.ACTION_DELETE)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse("package:$packageName"))
                                )
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                }.show()
            }
        }

        appsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = appsAdapter
        }

        appsViewModel.getInstalledApps(prefs.showSystemApps()).observe(
            viewLifecycleOwner,
            {
                when (it.status) {
                    Status.LOADING -> {
                        it.data?.let { data ->
                            appsView.setItemViewCacheSize(data.size)
                            appsAdapter.items = data

                            if (data.isNotEmpty()) {
                                loadingView.visibility = View.GONE
                            }
                        }
                    }
                    Status.SUCCESS -> {
                        checkNotNull(it.data)
                        appsView.setItemViewCacheSize(it.data.size)
                        appsAdapter.items = it.data
                        loadingView.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        loadingView.visibility = View.GONE
                    }
                }
            }
        )
    }

    private fun NavController.navigateToSettingsFragment() {
        navigate(LauncherFragmentDirections.toSettingsFragment())
    }
}
