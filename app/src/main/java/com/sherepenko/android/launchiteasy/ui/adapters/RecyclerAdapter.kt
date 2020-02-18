package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {

    fun onItemClick(view: View, position: Int, id: Long)
}

abstract class BaseRecyclerAdapter<T, VH : BaseRecyclerViewHolder<T>>(
    items: List<T> = listOf(),
    var itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<VH>(),
    OnItemClickListener {

    open var items: List<T> = items
        set(newItems) {
            field = newItems
            notifyDataSetChanged()
        }

    init {
        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bindItem(items[position])

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onItemClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemClick(view, position, id)
    }
}

abstract class BaseRecyclerViewHolder<T>(
    itemView: View,
    itemClickListener: OnItemClickListener? = null
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemClickListener?.apply {
            itemView.setOnClickListener { view ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    this.onItemClick(view, adapterPosition, itemId)
                }
            }
        }
    }

    abstract fun bindItem(item: T)
}