package com.datastore

import android.view.View
import androidx.core.view.isVisible

fun View.visible(){
    if (isVisible)return
    visibility = View.VISIBLE
}

fun View.gone(){
    if (!isVisible)return
    visibility = View.GONE
}