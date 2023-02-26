package com.datastore

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<M, V : ViewDataBinding> constructor(
    val context: Context,
    open val viewBinding: V
) :
    RecyclerView.ViewHolder(viewBinding.root) {

    open fun onCreateView(model: M, position: Int) {

    }
}