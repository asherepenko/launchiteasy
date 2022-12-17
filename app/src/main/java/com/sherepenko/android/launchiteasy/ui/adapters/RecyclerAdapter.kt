package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {

    fun onItemClick(view: View, position: Int, id: Long)
}

interface OnItemLongClickListener {

    fun onItemLongClick(view: View, position: Int, id: Long)
}

abstract class BaseRecyclerAdapter<T : Any, VH : BaseRecyclerViewHolder<T>>(
    var itemClickListener: OnItemClickListener? = null,
    var itemLongClickListener: OnItemLongClickListener? = null,
    diffCallback: DiffUtil.ItemCallback<T> = createDiffCallback()
) : ListAdapter<T, VH>(diffCallback),
    OnItemClickListener,
    OnItemLongClickListener {

    companion object {
        private fun <T : Any> createDiffCallback(): DiffUtil.ItemCallback<T> =
            object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                    areItemsTheSame(oldItem, newItem)
            }
    }

    init {
        this@BaseRecyclerAdapter.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long =
        position.toLong()

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bindItem(getItem(position))

    override fun onItemClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemClick(view, position, id)
    }

    override fun onItemLongClick(view: View, position: Int, id: Long) {
        itemLongClickListener?.onItemLongClick(view, position, id)
    }
}

abstract class BaseRecyclerViewHolder<T>(
    itemView: View,
    itemClickListener: OnItemClickListener? = null,
    itemLongClickListener: OnItemLongClickListener? = null
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemClickListener?.let {
            itemView.setOnClickListener { view ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    it.onItemClick(view, adapterPosition, itemId)
                }
            }
        }

        itemLongClickListener?.let {
            itemView.setOnLongClickListener { view ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    it.onItemLongClick(view, adapterPosition, itemId)
                    return@setOnLongClickListener true
                }

                return@setOnLongClickListener false
            }
        }
    }

    abstract fun bindItem(item: T)
}
