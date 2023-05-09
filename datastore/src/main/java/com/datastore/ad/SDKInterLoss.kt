package com.datastore.ad

import com.datastore.BaseActivity

class SDKInterLoss(private val baseActivity: BaseActivity<*>) {

    private val sdkInterAdMob by lazy { SDKInter(baseActivity) }
    private val sdkInterGaMob by lazy { SDKInter(baseActivity) }
    private var priority = AdPriority.AD_MOD
    private var oder = AdLoadOder.SEQUENTIALLY

    fun setListener(listener: SDKInter.SDKInterListener) {
        sdkInterAdMob.setListener(listener)
        sdkInterGaMob.setListener(listener)
    }

    fun setAdRefresh(isAdRefresh: Boolean) {
        sdkInterAdMob.setAdRefresh(isAdRefresh)
        sdkInterGaMob.setAdRefresh(isAdRefresh)
    }

    fun setAdPriority(priority: AdPriority): SDKInterLoss {
        this.priority = priority
        return this
    }

    fun setAdLoadOder(oder: AdLoadOder): SDKInterLoss {
        this.oder = oder
        return this
    }

    fun setAdUnitId(idUnitAdMob: String, idUnitGaMob: String): SDKInterLoss {
        sdkInterAdMob.setAdUnitId(idUnitAdMob)
        sdkInterGaMob.setAdUnitId(idUnitGaMob)
        return this
    }

    fun isAdLoaded(): Boolean {
        return sdkInterAdMob.isAdLoaded() || sdkInterGaMob.isAdLoaded()
    }

    fun isAdShowing(): Boolean {
        return sdkInterAdMob.isShowing() || sdkInterGaMob.isShowing()
    }

    fun isAdError(): Boolean {
        return sdkInterAdMob.isAdError() && sdkInterGaMob.isAdError()
    }

    fun destroyAd() {
        sdkInterAdMob.destroyAd()
        sdkInterGaMob.destroyAd()
    }

    fun loadAd() {
        when (oder) {
            AdLoadOder.PARALLEL -> {
                sdkInterAdMob.loadAd()
                sdkInterGaMob.loadAd()
            }

            AdLoadOder.SEQUENTIALLY -> {
                sdkInterAdMob.loadAd {
                    if (sdkInterAdMob.isAdError()) {
                        sdkInterGaMob.loadAd()
                    }
                }
            }
        }
    }

    fun showAd(): Boolean {
        if (sdkInterAdMob.isAdError() && sdkInterGaMob.isAdError()) {
            return false
        }
        return when (priority) {
            AdPriority.AD_MOD -> {
                if (sdkInterAdMob.isAdLoaded()) {
                    sdkInterAdMob.showAd()
                    true
                } else if (sdkInterGaMob.isAdLoaded()) {
                    sdkInterGaMob.showAd()
                    true
                } else false
            }

            AdPriority.GA_MOB -> {
                if (sdkInterGaMob.isAdLoaded()) {
                    sdkInterGaMob.showAd()
                    true
                } else if (sdkInterAdMob.isAdLoaded()) {
                    sdkInterAdMob.showAd()
                    true
                } else false
            }
        }
    }
}