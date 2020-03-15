package com.sherepenko.android.launchiteasy.ui.adapters

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.utils.inflate
import kotlinx.android.synthetic.main.item_app.view.appIconView
import kotlinx.android.synthetic.main.item_app.view.appLabelView

class AppsAdapter : BaseRecyclerAdapter<AppItem, AppsAdapter.ViewHolder>() {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppItem>() {
            override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem == newItem
        }
    }

    private val listDiffer = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_app))

    override fun getItemCount(): Int =
        listDiffer.currentList.size

    override fun getItemId(position: Int): Long =
        getPackageName(position).hashCode().toLong()

    override fun getItem(position: Int): AppItem =
        listDiffer.currentList[position]

    override fun notifyItemsChanged(oldItems: List<AppItem>, newItems: List<AppItem>) {
        listDiffer.submitList(newItems)
    }

    fun getPackageName(position: Int): String =
        getItem(position).packageName

    inner class ViewHolder(
        itemView: View
    ) : BaseRecyclerViewHolder<AppItem>(itemView, this@AppsAdapter) {

        override fun bindItem(item: AppItem) {
            itemView.apply {
                appIconView.setImageDrawable(item.getApplicationIcon(context))
                appLabelView.text = item.label
            }
        }
    }

    fun AppItem.getApplicationIcon(context: Context): Drawable? =
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
}
