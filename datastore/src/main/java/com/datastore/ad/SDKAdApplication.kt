package com.datastore.ad

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.datastore.BaseApplication

class SDKAdApplication(
    private val application: BaseApplication,
    private val sdkAppOpenLoss: SDKAppOpenLoss,
    private val idUnitAdMob: String,
    private val idUnitGaMob: String
) : SDKAppOpen.SDKAppOpenListener {

    interface SDKAdApplicationListener {
        fun onSDKAdApplicationAdShow(sdkAppOpen: SDKAppOpenLoss)
        fun onSDKAdApplicationAdDisplayed()
        fun onSDKAdApplicationAdDismissed()
    }

    private var isLock = false
    private var listener: SDKAdApplicationListener? = null

    private fun setAdUnitId() {
        sdkAppOpenLoss.setAdUnitId(idUnitAdMob, idUnitGaMob)
        sdkAppOpenLoss.setAdRefresh(true)
        sdkAppOpenLoss.setListener(this)
        sdkAppOpenLoss.loadAd()
    }

    fun setListener(listener: SDKAdApplicationListener? = null) {
        this.listener = listener
    }

    fun addLifecycleObserver() {
        setAdUnitId()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    fun removeLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleEventObserver)
    }

    fun lockLifecycleObserver() {
        isLock = true
    }

    fun unLockLifecycleObserver() {
        isLock = false
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_START && !isLock && !application.isPremium()) {
            if (sdkAppOpenLoss.isAdLoaded()) {
                if (!sdkAppOpenLoss.isAdShowing()) listener?.onSDKAdApplicationAdShow(sdkAppOpenLoss)
            } else {
                sdkAppOpenLoss.loadAd()
            }
        }
    }

    override fun onAdShowedFullScreenContent() {
        listener?.onSDKAdApplicationAdDisplayed()
    }

    override fun onAdDismissedFullScreenContent(isDisplayAd: Boolean) {
        listener?.onSDKAdApplicationAdDismissed()
    }
}