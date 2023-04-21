package com.datastore

import android.view.View
import androidx.core.view.isVisible

fun View.visible() {
    if (isVisible) return
    visibility = View.VISIBLE
}

fun View.gone() {
    if (visibility == View.GONE) return
    visibility = View.GONE
}

fun View.invisible() {
    if (visibility == View.INVISIBLE) return
    visibility = View.INVISIBLE
}