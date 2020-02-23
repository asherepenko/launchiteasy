package com.sherepenko.android.launchiteasy.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.AppsAdapter
import com.sherepenko.android.launchiteasy.ui.adapters.OnItemClickListener
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.appsView
import kotlinx.android.synthetic.main.fragment_launcher.loadingView
import kotlinx.android.synthetic.main.fragment_launcher.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherFragment : BaseFragment(R.layout.fragment_launcher) {

    private val appsViewModel: AppsViewModel by viewModel()

    private lateinit var appsAdapter: AppsAdapter

    private var selectedAppPosition = RecyclerView.NO_POSITION

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        appsAdapter = AppsAdapter()
        appsAdapter.itemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, id: Long) {
                // ignore
            }

            override fun onItemLongClick(view: View, position: Int, id: Long) {
                selectedAppPosition = position
                val popupMenu = PopupMenu(requireActivity(), view, Gravity.TOP)
                popupMenu.inflate(R.menu.app_context_menu)

                popupMenu.setOnMenuItemClickListener {
                    val packageName = appsAdapter.getPackageName(selectedAppPosition)

                    when (it.itemId) {
                        R.id.actionAppInfo -> {
                            if (selectedAppPosition != RecyclerView.NO_POSITION) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setData(Uri.parse("package:$packageName"))

                                intent.resolveActivity(requireContext().packageManager)?.let {
                                    startActivity(intent)
                                }
                            }
                        }
                        R.id.actionAppUninstall -> {
                            val intent = Intent(Intent.ACTION_DELETE)
                                .setData(Uri.parse("package:$packageName"))

                            intent.resolveActivity(requireContext().packageManager)?.let {
                                startActivity(intent)
                            }
                        }
                        else -> {
                            // ignore
                        }
                    }
                    true
                }

                popupMenu.setOnDismissListener {
                    selectedAppPosition = RecyclerView.NO_POSITION
                }

                popupMenu.show()
            }
        }

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

    private fun setupToolbar() {
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarView)
        }
    }
}
