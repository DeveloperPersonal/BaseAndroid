package com.datastore.ad

import android.widget.FrameLayout
import com.datastore.BaseActivity
import com.datastore.gone

class SDKBannerInlineLoss(private val baseActivity: BaseActivity<*>) {

    private val sdkBannerAdMob by lazy { SDKBannerInline(baseActivity) }
    private val sdkBannerGaMob by lazy { SDKBannerInline(baseActivity) }
    private var priority = AdPriority.AD_MOD

    fun setListener(listener: SDKBannerInline.SDKBannerListener) {
        sdkBannerAdMob.setListener(listener)
        sdkBannerGaMob.setListener(listener)
    }

    fun setAdPriority(priority: AdPriority): SDKBannerInlineLoss {
        this.priority = priority
        return this
    }

    fun setAdUnitId(idUnitAdMob: String, idUnitGaMob: String): SDKBannerInlineLoss {
        sdkBannerAdMob.setAdUnitId(idUnitAdMob)
        sdkBannerGaMob.setAdUnitId(idUnitGaMob)
        return this
    }

    fun loadAd(
        adContainerView: FrameLayout,
        width: Int,
        height: Int
    ) {
        when (priority) {
            AdPriority.AD_MOD -> {
                sdkBannerAdMob.loadAd(adContainerView, width, height) {
                    sdkBannerGaMob.loadAd(adContainerView, width, height) {
                        adContainerView.gone()
                    }
                }
            }

            AdPriority.GA_MOB -> {
                sdkBannerGaMob.loadAd(adContainerView, width, height) {
                    sdkBannerAdMob.loadAd(adContainerView, width, height) {
                        adContainerView.gone()
                    }
                }
            }
        }
    }
}