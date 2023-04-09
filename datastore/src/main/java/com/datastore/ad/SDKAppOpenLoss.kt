package com.datastore.ad

import android.content.Context
import com.datastore.BaseActivity

class SDKAppOpenLoss(private val context: Context) {

    private val sdkAppOpenAdMob by lazy { SDKAppOpen(context) }
    private val sdkAppOpenGaMob by lazy { SDKAppOpen(context) }
    private var priority = AdPriority.AD_MOD
    private var oder = AdLoadOder.PARALLEL

    fun setListener(listener: SDKAppOpen.SDKAppOpenListener) {
        sdkAppOpenAdMob.setListener(listener)
        sdkAppOpenGaMob.setListener(listener)
    }

    fun setAdPriority(priority: AdPriority): SDKAppOpenLoss {
        this.priority = priority
        return this
    }

    fun setAdRefresh(isAdRefresh: Boolean) {
        sdkAppOpenAdMob.setAdRefresh(isAdRefresh)
        sdkAppOpenGaMob.setAdRefresh(isAdRefresh)
    }

    fun setAdLoadOder(oder: AdLoadOder): SDKAppOpenLoss {
        this.oder = oder
        return this
    }

    fun setAdUnitId(idUnitAdMob: String, idUnitGaMob: String): SDKAppOpenLoss {
        sdkAppOpenAdMob.setAdUnitId(idUnitAdMob)
        sdkAppOpenGaMob.setAdUnitId(idUnitGaMob)
        return this
    }

    fun isAdLoaded(): Boolean {
        return sdkAppOpenAdMob.isAdLoaded() || sdkAppOpenGaMob.isAdLoaded()
    }

    fun isAdShowing(): Boolean {
        return sdkAppOpenAdMob.isAdShowing() || sdkAppOpenGaMob.isAdShowing()
    }

    fun isAdError(): Boolean {
        return sdkAppOpenAdMob.isAdError() && sdkAppOpenGaMob.isAdError()
    }

    fun loadAd() {
        when (oder) {
            AdLoadOder.PARALLEL -> {
                sdkAppOpenAdMob.loadAd {
                    if (sdkAppOpenAdMob.isAdError()) {
                        sdkAppOpenGaMob.loadAd()
                    }
                }
            }
            AdLoadOder.SEQUENTIALLY -> {
                sdkAppOpenAdMob.loadAd()
                sdkAppOpenGaMob.loadAd()
            }
        }
    }

    fun showAd(activity: BaseActivity<*>): Boolean {
        if (sdkAppOpenAdMob.isAdError() && sdkAppOpenGaMob.isAdError()) {
            return false
        }
        return when (priority) {
            AdPriority.AD_MOD -> {
                if (sdkAppOpenAdMob.isAdLoaded()) {
                    sdkAppOpenAdMob.showAd(activity)
                    true
                } else if (sdkAppOpenGaMob.isAdLoaded()) {
                    sdkAppOpenGaMob.showAd(activity)
                    true
                } else false
            }
            AdPriority.GA_MOB -> {
                if (sdkAppOpenGaMob.isAdLoaded()) {
                    sdkAppOpenGaMob.showAd(activity)
                    true
                } else if (sdkAppOpenAdMob.isAdLoaded()) {
                    sdkAppOpenAdMob.showAd(activity)
                    true
                } else false
            }
        }
    }
}