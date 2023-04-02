package com.datastore.ad

import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.ad.SDKConfig.ID_UNIT_APP_OPEN_TEST
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

//implementation 'com.google.android.gms:play-services-ads:21.5.0'

class SDKAppOpen(private val baseActivity: BaseActivity<*>) {

    interface SDKAppOpenListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad(msg: String) {}
        fun onAdShowedFullScreenContent() {}
        fun onAdDismissedFullScreenContent(isDisplayAd: Boolean) {}
    }

    private var listener: SDKAppOpenListener? = null
    private var appOpenAd: AppOpenAd? = null

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
    fun setAdRefresh(isAdRefresh: Boolean): SDKAppOpen {
        this.isAdRefresh = isAdRefresh
        return this
    }

    fun setAdUnitId(idUnit: String): SDKAppOpen {
        this.idUnit = idUnit
        if (BuildConfig.DEBUG) {
            this.idUnit = ID_UNIT_APP_OPEN_TEST
        }
        return this
    }

    fun setListener(listener: SDKAppOpenListener): SDKAppOpen {
        this.listener = listener
        return this
    }

    fun isAdLoaded(): Boolean {
        return appOpenAd != null
    }

    fun isAdError(): Boolean {
        return isError
    }

    fun isShowing(): Boolean {
        return isShowing
    }

    fun loadAd() {
        if (idUnit.isEmpty()) {
            throw NullPointerException("Advertising id cannot be blank")
        }
        if (isLoadingAd || isAdLoaded()) return
        isError = false
        isLoadingAd = true
        AppOpenAd.load(
            baseActivity,
            idUnit,
            SDKConfig.getAdRequest(), object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    isError = false
                    appOpenAd = p0
                    isLoadingAd = false
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isError = true
                    appOpenAd = null
                    isLoadingAd = false
                    listener?.onAdFailedToLoad(p0.message)
                }
            })
    }

    fun showAd(activity: BaseActivity<*>) {
        if (isAdError() || !isAdLoaded()) {
            listener?.onAdDismissedFullScreenContent(false)
            return
        }
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
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
        appOpenAd?.show(activity)
    }

    fun destroyAd() {
        listener = null
        clearAd()
    }

    private fun clearAd() {
        isShowing = false
        isError = false
        appOpenAd?.fullScreenContentCallback = null
        appOpenAd = null
        isLoadingAd = false
    }

}