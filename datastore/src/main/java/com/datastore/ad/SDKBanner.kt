package com.datastore.ad

import android.annotation.SuppressLint
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.FrameLayout
import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.ad.SDKConfig.ID_UNIT_BANNER_ADMOB_TEST
import com.datastore.ad.SDKConfig.getAdRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


class SDKBanner(private val baseActivity: BaseActivity<*>) {

    interface SDKBannerListener {
        fun onAdLoaded() {}
        fun onAdFailedToLoad() {}
    }

    private var adView: AdView? = null
    private var tryAgainIfError = false
    private var countTryAgainIfError = 0
    private var listener: SDKBannerListener? = null
    private var idUnit: String = ""

    fun setAdUnitId(idUnit: String): SDKBanner {
        this.idUnit = idUnit
        if (BuildConfig.DEBUG) {
            this.idUnit = SDKConfig.ID_UNIT_APP_OPEN_TEST
        }
        return this
    }

    fun setListener(listener: SDKBannerListener): SDKBanner {
        this.listener = listener
        return this
    }

    /**
     * Default false no try again if error
     * */
    fun setTryAgainIfError(isCall: Boolean) {
        tryAgainIfError = isCall
    }

    @SuppressLint("MissingPermission")
    fun loadAd(
        adContainerView: FrameLayout,
        adLoadError: (() -> Unit)? = null
    ) {
        adView = AdView(baseActivity).apply {
            adUnitId = idUnit
            setAdSize(baseActivity.getAdSize(adContainerView))
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    listener?.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    if (!baseActivity.exist()) return
                    if (tryAgainIfError && countTryAgainIfError <= 2) {
                        countTryAgainIfError++
                        loadAd(adContainerView)
                    } else {
                        adLoadError?.let { it() }
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