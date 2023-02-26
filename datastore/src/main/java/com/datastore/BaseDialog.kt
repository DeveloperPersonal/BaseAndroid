package com.datastore

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseDialog<T : ViewDataBinding>(open val activity: BaseActivity<*>) :
    AlertDialog(activity) {

    open val binding: T by lazy {
        DataBindingUtil.inflate(LayoutInflater.from(context), onLayoutId(), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lifecycleOwner = activity
        setCanceledOnTouchOutside(setCanceledOnTouchOutside())
        setCancelable(setCancelable())
        window?.setLayout(getWidth(), getHeight())
        if (animator() != -1) {
            window?.setWindowAnimations(animator())
        }
        window?.setGravity(gravity())
        if (isTransparent()) {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        onCreateUI()
    }

    protected abstract fun onLayoutId(): Int
    protected abstract fun onCreateUI()

    open fun showIfReady() {
        if (isShowing || !activity.exist()) return
        show()
    }

    open fun dismissIfReady() {
        if (!isShowing || !activity.exist()) return
        dismiss()
    }

    open fun isTransparent(): Boolean {
        return false
    }

    open fun getWidth(): Int {
        return ViewGroup.LayoutParams.MATCH_PARENT
    }

    open fun getHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun animator(): Int {
        return R.style.scale
    }

    open fun gravity(): Int {
        return Gravity.CENTER
    }

    open fun setCanceledOnTouchOutside(): Boolean {
        return false
    }

    open fun setCancelable(): Boolean {
        return false
    }
}