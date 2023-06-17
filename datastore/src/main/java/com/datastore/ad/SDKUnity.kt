package com.datastore.ad

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

object SDKUnity {

    enum class SDKUnityState {
        SHOW, CLOSE
    }

    var unitId: String = ""
    private var isLoadingAd = false
    private var placementId = ""
    private var onUnityAdsShowStart: ((SDKUnityState) -> Unit)? = null

    fun initialize(context: Context, gameId: String, block: (() -> Unit)) {
        UnityAds.initialize(context, gameId, false, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                block()
            }

            override fun onInitializationFailed(
                error: UnityAds.UnityAdsInitializationError?, message: String?
            ) {

            }
        })
    }

    fun loadAd(block: (String) -> Unit) {
        if (isLoadingAd) return
        isLoadingAd = true
        placementId = ""
        UnityAds.load(unitId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String?) {
                isLoadingAd = false
                SDKUnity.placementId = placementId ?: ""
                block("true")
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String?, error: UnityAds.UnityAdsLoadError?, message: String?
            ) {
                isLoadingAd = false
                block("false: $error $message")
                SDKUnity.placementId = ""
            }
        })
    }

    fun isLoadedAd() = placementId.isNotEmpty()

    fun onUnityAdsListener(onUnityAdsShowStart: ((SDKUnityState) -> Unit)) {
        this.onUnityAdsShowStart = onUnityAdsShowStart
    }

    fun showAd(activity: AppCompatActivity, block: () -> Unit) {
        if (!isLoadedAd()) {
            onUnityAdsShowStart?.let { it(SDKUnityState.CLOSE) }
            loadAd {}
            block()
            return
        }
        onUnityAdsShowStart?.let { it(SDKUnityState.SHOW) }
        UnityAds.show(activity, placementId, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String?, error: UnityAds.UnityAdsShowError?, message: String?
            ) {
                SDKUnity.placementId = ""
                loadAd {}
                onUnityAdsShowStart?.let { it(SDKUnityState.CLOSE) }
                block()
            }

            override fun onUnityAdsShowStart(placementId: String?) {
                onUnityAdsShowStart?.let { it(SDKUnityState.SHOW) }
            }

            override fun onUnityAdsShowClick(placementId: String?) {

            }

            override fun onUnityAdsShowComplete(
                placementId: String?, state: UnityAds.UnityAdsShowCompletionState?
            ) {
                SDKUnity.placementId = ""
                loadAd {}
                onUnityAdsShowStart?.let { it(SDKUnityState.CLOSE) }
                block()
            }

        })
        placementId = ""
    }
}