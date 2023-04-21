package com.datastore.ad

import com.datastore.BaseActivity

class SDKRewardedLoss(private val baseActivity: BaseActivity<*>) {

    private val sdkRewardedAdmob by lazy { SDKRewarded(baseActivity) }
    private val sdkRewardedGaMob by lazy { SDKRewarded(baseActivity) }
    private var priority = AdPriority.AD_MOD
    private var oder = AdLoadOder.PARALLEL

    fun setListener(listener: SDKRewarded.SDKRewardedListener) {
        sdkRewardedAdmob.setListener(listener)
        sdkRewardedGaMob.setListener(listener)
    }

    fun setAdPriority(priority: AdPriority): SDKRewardedLoss {
        this.priority = priority
        return this
    }

    fun setAdLoadOder(oder: AdLoadOder): SDKRewardedLoss {
        this.oder = oder
        return this
    }

    fun setAdUnitId(idUnitAdMob: String, idUnitGaMob: String): SDKRewardedLoss {
        sdkRewardedAdmob.setAdUnitId(idUnitAdMob)
        sdkRewardedGaMob.setAdUnitId(idUnitGaMob)
        return this
    }

    fun isAdLoaded(): Boolean {
        return sdkRewardedAdmob.isAdLoaded() || sdkRewardedGaMob.isAdLoaded()
    }

    fun isAdShowing(): Boolean {
        return sdkRewardedAdmob.isShowing() || sdkRewardedGaMob.isShowing()
    }

    fun isAdError(): Boolean {
        return sdkRewardedAdmob.isAdError() && sdkRewardedGaMob.isAdError()
    }

    fun loadAd() {
        when (oder) {
            AdLoadOder.PARALLEL -> {
                sdkRewardedAdmob.loadAd {
                    if (sdkRewardedAdmob.isAdError()) {
                        sdkRewardedGaMob.loadAd()
                    }
                }
            }

            AdLoadOder.SEQUENTIALLY -> {
                sdkRewardedAdmob.loadAd()
                sdkRewardedGaMob.loadAd()
            }
        }
    }

    fun showAd(): Boolean {
        if (isAdError()) {
            return false
        }
        return when (priority) {
            AdPriority.AD_MOD -> {
                if (sdkRewardedAdmob.isAdLoaded()) {
                    sdkRewardedAdmob.showAd()
                    true
                } else if (sdkRewardedGaMob.isAdLoaded()) {
                    sdkRewardedGaMob.showAd()
                    true
                } else false
            }

            AdPriority.GA_MOB -> {
                if (sdkRewardedGaMob.isAdLoaded()) {
                    sdkRewardedGaMob.showAd()
                    true
                } else if (sdkRewardedAdmob.isAdLoaded()) {
                    sdkRewardedAdmob.showAd()
                    true
                } else false
            }
        }
    }
}