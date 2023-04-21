package com.datastore.ad

import com.datastore.BaseActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

class SDKRewarded(private val baseActivity: BaseActivity<*>) {

    interface SDKRewardedListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad(msg: String) {}
        fun onAdShowedFullScreenContent() {}
        fun onAdDismissedFullScreenContent(isRewardedItem: Boolean) {}
    }

    private var listener: SDKRewarded.SDKRewardedListener? = null
    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd = false
    private var isError = false
    private var isShowing = false
    private var isAdRefresh = false
    private var idUnit: String = ""

    fun setAdRefresh(isAdRefresh: Boolean): SDKRewarded {
        this.isAdRefresh = isAdRefresh
        return this
    }

    fun setAdUnitId(idUnit: String): SDKRewarded {
        this.idUnit = idUnit
        return this
    }

    fun setListener(listener: SDKRewarded.SDKRewardedListener): SDKRewarded {
        this.listener = listener
        return this
    }

    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }

    fun isAdError(): Boolean {
        return isError
    }

    fun isShowing(): Boolean {
        return isShowing
    }

    fun loadAd(complete: (() -> Unit)? = null) {
        if (idUnit.isEmpty()) {
            throw NullPointerException("Advertising id cannot be blank")
        }
        if (isLoadingAd || isAdLoaded()) return
        isError = false
        isLoadingAd = true
        RewardedAd.load(
            baseActivity,
            idUnit,
            SDKConfig.getAdRequest(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    isError = false
                    rewardedAd = p0
                    isLoadingAd = false
                    complete?.let { it() }
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isError = true
                    rewardedAd = null
                    isLoadingAd = false
                    complete?.let { it() }
                    listener?.onAdFailedToLoad(p0.message)
                }
            })
    }

    fun showAd() {
        val isRewardedItem = AtomicBoolean(false)
        if (isAdError() || !isAdLoaded()) {
            listener?.onAdDismissedFullScreenContent(false)
            return
        }
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowing = true
                listener?.onAdShowedFullScreenContent()
            }

            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                listener?.onAdDismissedFullScreenContent(isRewardedItem.get())
                clearAd()
                if (isAdRefresh) loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                isShowing = false
                listener?.onAdDismissedFullScreenContent(isRewardedItem.get())
                clearAd()
                if (isAdRefresh) loadAd()
            }
        }
        isShowing = true
        rewardedAd?.show(baseActivity) {
            isRewardedItem.set(true)
        }
    }

    fun destroyAd() {
        listener = null
        clearAd()
    }

    private fun clearAd() {
        isShowing = false
        isError = false
        rewardedAd?.fullScreenContentCallback = null
        rewardedAd = null
        isLoadingAd = false
    }
}