package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {

    fun onItemClick(view: View, position: Int, id: Long)

    fun onItemLongClick(view: View, position: Int, id: Long)
}

abstract class BaseRecyclerAdapter<T, VH : BaseRecyclerViewHolder<T>>(
    diffCallback: DiffUtil.ItemCallback<T>,
    var itemClickListener: OnItemClickListener? = null
) : ListAdapter<T, VH>(diffCallback),
    OnItemClickListener {

    init {
        this@BaseRecyclerAdapter.setHasStableIds(true)
    }

    private var rootView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rootView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        rootView = null
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bindItem(getItem(position))

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {
        super.onCurrentListChanged(previousList, currentList)
        rootView?.requestLayout()
    }

    override fun onItemClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemClick(view, position, id)
    }

    override fun onItemLongClick(view: View, position: Int, id: Long) {
        itemClickListener?.onItemLongClick(view, position, id)
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
