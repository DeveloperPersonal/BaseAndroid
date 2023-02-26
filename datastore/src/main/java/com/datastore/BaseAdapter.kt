package com.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<M, VB : ViewDataBinding, VH : BaseViewHolder<M, VB>>(val context: Context) :
    RecyclerView.Adapter<VH>() {

    open var currentList: MutableList<M> = arrayListOf()

    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    fun submitWithClearList(list: MutableList<M>, reload: Boolean = false) {
        currentList.clear()
        currentList.addAll(list)
        if (reload) {
            reload()
        }
    }

    fun submit(list: MutableList<M>, reload: Boolean = false) {
        currentList.addAll(list)
        if (reload) {
            reload()
        }
    }

    fun submit(m: M, reload: Boolean = false) {
        currentList.add(m)
        if (reload) {
            reload()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        notifyDataSetChanged()
    }

    fun reloadAnimate(position: Int) {
        notifyItemChanged(position)
    }

    fun insertReloadAnimate(position: Int) {
        notifyItemInserted(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun deleteReloadAnimate(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding = DataBindingUtil.inflate<VB>(
            layoutInflater,
            onLayoutId(),
            parent,
            false
        )
        return onCreateViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onCreateView(holder.viewBinding, currentList[position], position)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    protected abstract fun onCreateViewHolder(viewBinding: VB): VH

    @LayoutRes
    protected abstract fun onLayoutId(): Int

    protected abstract fun onCreateView(viewBinding: VB, m: M, position: Int)

}