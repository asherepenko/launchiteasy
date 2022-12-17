package com.sherepenko.android.launchiteasy.ui.adapters

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.databinding.ItemAppBinding
import timber.log.Timber

class AppsAdapter : BaseRecyclerAdapter<AppItem, AppsAdapter.ViewHolder>(
    diffCallback = DIFF_CALLBACK
) {

    companion object {
        private const val TAG = "AppsAdapter"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppItem>() {
            override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemId(position: Int): Long =
        getPackageName(position).hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemAppBinding.inflate(LayoutInflater.from(parent.context)))

    fun getPackageName(position: Int): String =
        getItem(position).packageName

    inner class ViewHolder(
        private val binding: ItemAppBinding
    ) : BaseRecyclerViewHolder<AppItem>(binding.root, this) {

        override fun bindItem(item: AppItem) {
            binding.apply {
                appIconView.load(item.getApplicationIcon()) {
                    crossfade(true)
                }
                appLabelView.text = item.label
            }
        }

        private fun AppItem.getApplicationIcon(): Drawable? =
            try {
                itemView.context.packageManager.getApplicationIcon(packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.tag(TAG).e(e, "$packageName not found")
                null
            }
    }
}
