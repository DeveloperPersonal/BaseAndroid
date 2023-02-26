package com.datastore

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope

/**
    implementation 'com.intuit.sdp:sdp-android:1.1.0'


    implementation "io.insert-koin:koin-core:3.1.5"


    implementation "io.insert-koin:koin-android:3.1.5"


    testImplementation "io.insert-koin:koin-test:3.1.5"
* */

abstract class BaseActivity<V : ViewDataBinding> : AppCompatActivity(),
    AndroidScopeComponent {

    override val scope: Scope by createScope()

    private var resume = false

    open val binding: V by lazy {
        DataBindingUtil.setContentView<V>(this, onLayoutId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        onCreateUI()
    }

    override fun onResume() {
        resume = true
        super.onResume()
    }

    override fun onPause() {
        resume = false
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
        scope.close()
    }

    @LayoutRes
    protected abstract fun onLayoutId(): Int
    protected abstract fun onCreateUI()

    open fun exist(): Boolean {
        return !isFinishing && !isDestroyed
    }

    open fun resume(): Boolean {
        return exist() && resume
    }
}