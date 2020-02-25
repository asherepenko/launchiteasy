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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.AppsAdapter
import com.sherepenko.android.launchiteasy.ui.adapters.OnItemClickListener
import com.sherepenko.android.launchiteasy.utils.PreferenceHelper
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.appsView
import kotlinx.android.synthetic.main.fragment_launcher.loadingView
import kotlinx.android.synthetic.main.fragment_launcher.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.inject

class LauncherFragment : BaseFragment(R.layout.fragment_launcher) {

    private val appsViewModel: AppsViewModel by viewModel()

    private val prefs: PreferenceHelper by inject()

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
                findNavController().navigate(
                    LauncherFragmentDirections.toSettingsFragment()
                )
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
        val appsAdapter = AppsAdapter()
        appsAdapter.itemClickListener = object :
            OnItemClickListener {
            override fun onItemClick(view: View, position: Int, id: Long) {
                val packageName = appsAdapter.getPackageName(position)

                requireActivity().packageManager.getLaunchIntentForPackage(packageName)?.let {
                    startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    requireActivity().overridePendingTransition(
                        R.anim.nav_default_enter_anim,
                        R.anim.nav_default_exit_anim
                    )
                }
            }

            override fun onItemLongClick(view: View, position: Int, id: Long) {
                val packageName = appsAdapter.getPackageName(position)

                PopupMenu(requireActivity(), view, Gravity.TOP).apply {
                    inflate(R.menu.app_details_menu)

                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.actionAppInfo -> {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setData(Uri.parse("package:$packageName"))

                                intent.resolveActivity(requireActivity().packageManager)?.let {
                                    startActivity(intent)
                                    requireActivity().overridePendingTransition(
                                        R.anim.nav_default_enter_anim,
                                        R.anim.nav_default_exit_anim
                                    )
                                }
                                true
                            }
                            R.id.actionAppUninstall -> {
                                val intent = Intent(Intent.ACTION_DELETE)
                                    .setData(Uri.parse("package:$packageName"))

                                intent.resolveActivity(requireActivity().packageManager)?.let {
                                    startActivity(intent)
                                    requireActivity().overridePendingTransition(
                                        R.anim.nav_default_enter_anim,
                                        R.anim.nav_default_exit_anim
                                    )
                                }
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

        appsViewModel.getInstalledApps().observe(viewLifecycleOwner, Observer {
            val showSystemApps = prefs.showSystemApps()
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        appsAdapter.items = data.filter { app ->
                            showSystemApps || !app.isSystem
                        }

                        if (data.isNotEmpty()) {
                            loadingView.visibility = View.GONE
                        }
                    }
                }
                Status.SUCCESS -> {
                    appsView.setItemViewCacheSize(it.data!!.size)
                    appsAdapter.items = it.data.filter { app ->
                        showSystemApps || !app.isSystem
                    }
                    loadingView.visibility = View.GONE
                }
                Status.ERROR -> {
                    loadingView.visibility = View.GONE
                }
            }
        })
    }
}
