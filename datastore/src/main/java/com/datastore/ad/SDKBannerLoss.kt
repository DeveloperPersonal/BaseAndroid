package com.datastore.ad

import android.widget.FrameLayout
import com.datastore.BaseActivity
import com.datastore.gone

class SDKBannerLoss(private val baseActivity: BaseActivity<*>) {

    private val sdkBannerAdMob by lazy { SDKBanner(baseActivity) }
    private val sdkBannerGaMob by lazy { SDKBanner(baseActivity) }
    private var priority = AdPriority.AD_MOD

    fun setListener(listener: SDKBanner.SDKBannerListener) {
        sdkBannerAdMob.setListener(listener)
        sdkBannerGaMob.setListener(listener)
    }

    fun setAdPriority(priority: AdPriority): SDKBannerLoss {
        this.priority = priority
        return this
    }

    fun setAdUnitId(idUnitAdMob: String, idUnitGaMob: String): SDKBannerLoss {
        sdkBannerAdMob.setAdUnitId(idUnitAdMob)
        sdkBannerGaMob.setAdUnitId(idUnitGaMob)
        return this
    }

    fun loadAd(adContainerView: FrameLayout) {
        when (priority) {
            AdPriority.AD_MOD -> {
                sdkBannerAdMob.loadAd(adContainerView) {
                    sdkBannerGaMob.loadAd(adContainerView) {
                        adContainerView.gone()
                    }
                }
            }
            AdPriority.GA_MOB -> {
                sdkBannerGaMob.loadAd(adContainerView) {
                    sdkBannerAdMob.loadAd(adContainerView) {
                        adContainerView.gone()
                    }
                }
            }
        }
    }
}