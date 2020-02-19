package com.sherepenko.android.launchiteasy.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.BaseRecyclerAdapter
import com.sherepenko.android.launchiteasy.ui.adapters.BaseRecyclerViewHolder
import com.sherepenko.android.launchiteasy.utils.inflate
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import kotlinx.android.synthetic.main.fragment_launcher.appsView
import kotlinx.android.synthetic.main.fragment_launcher.loadingView
import kotlinx.android.synthetic.main.item_app.view.iconView
import kotlinx.android.synthetic.main.item_app.view.labelView
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

    override var items: List<AppItem> = listOf()
        set(newItems) {
            val oldItems = field
            field = newItems
            notifyItemsChanged(oldItems, field)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_app))

    override fun onItemClick(view: View, position: Int, id: Long) {
        super.onItemClick(view, position, id)

        view.context.packageManager.getLaunchIntentForPackage(items[position].packageName)?.let {
            view.context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun notifyItemsChanged(oldItems: List<AppItem>, newItems: List<AppItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int =
                oldItems.size

            override fun getNewListSize(): Int =
                newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition].packageName == newItems[newItemPosition].packageName

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition] == newItems[newItemPosition]
        })

        diffResult.dispatchUpdatesTo(this@AppsAdapter)
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
