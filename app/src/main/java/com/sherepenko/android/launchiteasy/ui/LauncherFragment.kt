package com.sherepenko.android.launchiteasy.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.ui.adapters.BaseRecyclerAdapter
import com.sherepenko.android.launchiteasy.ui.adapters.BaseRecyclerViewHolder
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.utils.Status
import com.sherepenko.android.launchiteasy.ui.utils.inflate
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.*
import kotlinx.android.synthetic.main.item_app.view.iconView
import kotlinx.android.synthetic.main.item_app.view.labelView
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherFragment : Fragment() {

    private val appsViewModel: AppsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_launcher, container, false)

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
                    loadingView.visibility = View.VISIBLE
                    it.data?.let { data ->
                        appsAdapter.items = data

                        if (data.isNotEmpty()) {
                            loadingView.visibility = View.GONE
                        }
                    }
                }
                Status.SUCCESS -> {
                    appsAdapter.items = it.data!!
                    loadingView.visibility = View.GONE
                }
                Status.ERROR -> {
                    loadingView.visibility = View.GONE
                }
            }
        })
    }
}

class AppsAdapter : BaseRecyclerAdapter<AppItem, AppsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_app))

    override fun onItemClick(view: View, position: Int, id: Long) {
        super.onItemClick(view, position, id)

        view.context.packageManager.getLaunchIntentForPackage(items[position].packageName)?.let {
            view.context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    inner class ViewHolder(
        itemView: View
    ) : BaseRecyclerViewHolder<AppItem>(itemView, this@AppsAdapter) {

        override fun bindItem(item: AppItem) {
            itemView.apply {
                iconView.setImageDrawable(
                    context.packageManager.getApplicationIcon(item.packageName)
                )
                labelView.text = item.label
            }
        }
    }
}
