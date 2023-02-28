package com.datastore.sdk

import android.app.Activity
import android.content.Context
import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.sdk.SDKConfig.ID_UNIT_APP_OPEN_TEST
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

//implementation 'com.google.android.gms:play-services-ads:21.5.0'

class SDKAppOpen(private val context: Context) {

    interface SDKAppOpenListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad(msg: String) {}
        fun onAdShowedFullScreenContent(){}
        fun onAdDismissedFullScreenContent(isDisplayAd: Boolean){}
    }

    private var listener: SDKAppOpenListener? = null
    private var appOpenAd: AppOpenAd? = null

    private var isLoadingAd = false
    private var isError = false
    private var isShowing = false
    private var isNextAdLoad = false
    private var idUnit: String = ""

    /**
     * Sau khi close quảng cáo có tiếp tục tải quảng cáo mới không
     *
     * isNextAdLoad= true có tải tiếp
     *
     * và ngược lại
     * */
    fun setNextAdLoad(isNextAdLoad: Boolean): SDKAppOpen {
        this.isNextAdLoad = isNextAdLoad
        return this
    }

    fun setUnitId(idUnit: String): SDKAppOpen {
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

    fun isLoadedAd(): Boolean {
        return appOpenAd != null
    }

    fun isError(): Boolean {
        return isError
    }

    fun loadAd() {
        if (idUnit.isEmpty()) {
            throw NullPointerException("Advertising id cannot be blank")
        }
        if (isLoadingAd || isLoadedAd()) return
        isError = false
        isLoadingAd = true
        AppOpenAd.load(
            context,
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
        if (isError() || !isLoadedAd()) {
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
                if (isNextAdLoad) loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                isShowing = false
                listener?.onAdDismissedFullScreenContent(false)
                clearAd()
                if (isNextAdLoad) loadAd()
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