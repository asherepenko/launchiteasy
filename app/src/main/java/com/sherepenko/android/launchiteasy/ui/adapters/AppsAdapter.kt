package com.sherepenko.android.launchiteasy.ui.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.utils.inflate
import kotlinx.android.synthetic.main.item_app.view.appIconView
import kotlinx.android.synthetic.main.item_app.view.appLabelView

class AppsAdapter : BaseRecyclerAdapter<AppItem, AppsAdapter.ViewHolder>() {

    override var items: List<AppItem> = listOf()
        set(newItems) {
            val oldItems = field
            field = newItems
            notifyItemsChanged(oldItems, field)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_app))

    override fun getItemId(position: Int): Long =
        items[position].packageName.hashCode().toLong()

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
                appIconView.setImageDrawable(
                    context.packageManager.getApplicationIcon(item.packageName)
                )
                appLabelView.text = item.label
            }
        }
    }
}
