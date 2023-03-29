package com.datastore

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.Purchase
import com.datastore.billing.SDKBillingV4
import com.datastore.billing.SDKBillingV4Listener
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope

/**
implementation 'com.intuit.sdp:sdp-android:1.1.0'

implementation "io.insert-koin:koin-core:3.1.5"

implementation "io.insert-koin:koin-android:3.1.5"

testImplementation "io.insert-koin:koin-test:3.1.5"

implementation "com.android.billingclient:billing-ktx:4.1.0"
 * */

abstract class BaseActivity<V : ViewDataBinding> : AppCompatActivity(),
    AndroidScopeComponent, SDKBillingV4Listener {

    override val scope: Scope by createScope()

    private var resume = false
    private val sdkBillingV4 by inject<SDKBillingV4>()

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

    fun startBillingConnection() {
        sdkBillingV4.startConnection(this)
    }

    fun buyBilling(productId: String) {
        sdkBillingV4.buy(productId)
    }

    fun endBillingConnection() {
        sdkBillingV4.endConnection()
    }

    fun consumeBillingAsync(purchase: Purchase) {
        sdkBillingV4.consume(purchase)
    }
}