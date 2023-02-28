package com.datastore.sdk

import android.annotation.SuppressLint
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.gone
import com.datastore.sdk.SDKConfig.ID_UNIT_BANNER_ADMOB_TEST
import com.datastore.sdk.SDKConfig.getAdRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


class SDKBanner(private val activity: BaseActivity<*>) {

    interface SDKBannerListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad() {}
    }

    private var adView: AdView? = null
    private var tryAgainIfError = false
    private var countTryAgainIfError = 0
    private var listener: SDKBannerListener? = null

    fun setListener(listener: SDKBannerListener) {
        this.listener = listener
    }

    /**
     * Default false no try again if error
     * */
    fun setTryAgainIfError(isCall: Boolean) {
        tryAgainIfError = isCall
    }

    @SuppressLint("MissingPermission")
    fun loadAd(adContainerView: FrameLayout, idUnit: String = ID_UNIT_BANNER_ADMOB_TEST) {
        adView = AdView(activity).apply {
            adUnitId = if (BuildConfig.DEBUG) ID_UNIT_BANNER_ADMOB_TEST else idUnit
            setAdSize(activity.getAdSize(adContainerView))
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    if (tryAgainIfError && countTryAgainIfError <= 2) {
                        countTryAgainIfError++
                        loadAd(adContainerView, idUnit)
                    } else {
                        adContainerView.gone()
                        listener?.onAdFailedToLoad()
                    }
                }
            }
            adContainerView.removeAllViews()
            adContainerView.addView(this)
            loadAd(getAdRequest())
        }
    }

    fun destroyAd() {
        adView?.destroy()
        adView = null
    }

    private fun BaseActivity<*>.getAdSize(adContainerView: FrameLayout): AdSize {
        var adWidthPixels: Float = adContainerView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
                val insets: android.graphics.Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                (windowMetrics.bounds.width() - insets.left - insets.right).toFloat()
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels.toFloat()
            }
        }
        val density: Float = resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

}