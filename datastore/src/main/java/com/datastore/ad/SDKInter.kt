package com.datastore.ad

import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.ad.SDKConfig.ID_UNIT_APP_OPEN_TEST
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

//implementation 'com.google.android.gms:play-services-ads:21.5.0'

class SDKInter(private val baseActivity: BaseActivity<*>) {

    interface SDKInterListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad(msg: String) {}
        fun onAdShowedFullScreenContent() {}
        fun onAdDismissedFullScreenContent(isDisplayAd: Boolean) {}
    }

    private var listener: SDKInterListener? = null
    private var interstitialAd: InterstitialAd? = null

    private var isLoadingAd = false
    private var isError = false
    private var isShowing = false
    private var isAdRefresh = false
    private var idUnit: String = ""

    /**
     * Sau khi close quảng cáo có tiếp tục tải quảng cáo mới không
     *
     * isAdRefresh= true có tải tiếp
     *
     * và ngược lại
     * */
    fun setAdRefresh(isAdRefresh: Boolean): SDKInter {
        this.isAdRefresh = isAdRefresh
        return this
    }

    fun setAdUnitId(idUnit: String): SDKInter {
        this.idUnit = idUnit
        if (BuildConfig.DEBUG) {
            this.idUnit = ID_UNIT_APP_OPEN_TEST
        }
        return this
    }

    fun setListener(listener: SDKInterListener): SDKInter {
        this.listener = listener
        return this
    }

    fun isAdLoaded(): Boolean {
        return interstitialAd != null
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
        InterstitialAd.load(
            baseActivity,
            idUnit,
            SDKConfig.getAdRequest(), object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    isError = false
                    interstitialAd = p0
                    isLoadingAd = false
                    complete?.let { it() }
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isError = true
                    interstitialAd = null
                    isLoadingAd = false
                    complete?.let { it() }
                    listener?.onAdFailedToLoad(p0.message)
                }
            })
    }

    fun showAd() {
        if (isAdError() || !isAdLoaded()) {
            listener?.onAdDismissedFullScreenContent(false)
            return
        }
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowing = true
                listener?.onAdShowedFullScreenContent()
            }

            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                listener?.onAdDismissedFullScreenContent(true)
                clearAd()
                if (isAdRefresh) loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                isShowing = false
                listener?.onAdDismissedFullScreenContent(false)
                clearAd()
                if (isAdRefresh) loadAd()
            }
        }
        isShowing = true
        interstitialAd?.show(baseActivity)
    }

    fun destroyAd() {
        listener = null
        clearAd()
    }

    private fun clearAd() {
        isShowing = false
        isError = false
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
        isLoadingAd = false
    }

}