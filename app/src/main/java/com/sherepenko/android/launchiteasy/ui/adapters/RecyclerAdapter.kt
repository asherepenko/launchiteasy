package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {

    fun onItemClick(view: View, position: Int, id: Long)

    fun onItemLongClick(view: View, position: Int, id: Long)
}

abstract class BaseRecyclerAdapter<T, VH : BaseRecyclerViewHolder<T>>(
    items: List<T> = listOf(),
    var itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<VH>(),
    OnItemClickListener {

    var items: List<T> = items
        set(newItems) {
            val oldItems = field
            field = newItems
            notifyItemsChanged(oldItems, field)
        }

    init {
        this@BaseRecyclerAdapter.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bindItem(getItem(position))

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onItemClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemClick(view, position, id)
    }

    override fun onItemLongClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemLongClick(view, position, id)
    }

    open fun getItem(position: Int) =
        items[position]

    open fun notifyItemsChanged(oldItems: List<T>, newItems: List<T>) {
        notifyDataSetChanged()
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
                    this@apply.onItemClick(view, adapterPosition, itemId)
                }
            }

            itemView.setOnLongClickListener { view ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    this@apply.onItemLongClick(view, adapterPosition, itemId)
                    return@setOnLongClickListener true
                }

                return@setOnLongClickListener false
            }
        }
    }

    abstract fun bindItem(item: T)
}
