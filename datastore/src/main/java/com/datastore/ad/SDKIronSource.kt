package com.datastore.ad

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener


/*<activity
android:name="com.ironsource.sdk.controller.ControllerActivity"
android:configChanges="orientation|screenSize"
android:hardwareAccelerated="true" />
<activity
android:name="com.ironsource.sdk.controller.InterstitialActivity"
android:configChanges="orientation|screenSize"
android:hardwareAccelerated="true"
android:theme="@android:style/Theme.Translucent" />
<activity
android:name="com.ironsource.sdk.controller.OpenUrlActivity"
android:configChanges="orientation|screenSize"
android:hardwareAccelerated="true"
android:theme="@android:style/Theme.Translucent" />
<provider
android:authorities="${applicationId}.IronsourceLifecycleProvider"
android:name="com.ironsource.lifecycle.IronsourceLifecycleProvider" />*/

object SDKIronSource {

    enum class SDKIronSourceState {
        SHOW, CLOSE
    }

    var unitId: String = ""
    private var onAdListener: (() -> Unit)? = null
    private var onIronSourceListener: ((SDKIronSourceState) -> Unit)? = null

    fun initialize(context: Context, appKey: String, block: (() -> Unit)) {
        IronSource.init(context, appKey) {
            loadAd()
            block()
        }
        IronSource.setLevelPlayInterstitialListener(object : LevelPlayInterstitialListener {
            // Invoked when the interstitial ad was loaded successfully.
            // AdInfo parameter includes information about the loaded ad
            override fun onAdReady(adInfo: AdInfo?) {

            }

            // Indicates that the ad failed to be loaded
            override fun onAdLoadFailed(error: IronSourceError) {

            }

            // Invoked when the Interstitial Ad Unit has opened, and user left the application screen.
            // This is the impression indication.
            override fun onAdOpened(adInfo: AdInfo?) {

            }

            // Invoked when the interstitial ad closed and the user went back to the application screen.
            override fun onAdClosed(adInfo: AdInfo?) {
                onAdListener?.let { it() }
                onIronSourceListener?.let { (it(SDKIronSourceState.CLOSE)) }
            }

            // Invoked when the ad failed to show
            override fun onAdShowFailed(error: IronSourceError?, adInfo: AdInfo?) {
                onAdListener?.let { it() }
                onIronSourceListener?.let { (it(SDKIronSourceState.CLOSE)) }
            }

            // Invoked when end user clicked on the interstitial ad
            override fun onAdClicked(adInfo: AdInfo?) {

            }

            // Invoked before the interstitial ad was opened, and before the InterstitialOnAdOpenedEvent is reported.
            // This callback is not supported by all networks, and we recommend using it only if
            // it's supported by all networks you included in your build.
            override fun onAdShowSucceeded(adInfo: AdInfo?) {
                onIronSourceListener?.let { (it(SDKIronSourceState.SHOW)) }
            }
        })
    }

    fun loadAd() {
        IronSource.loadInterstitial()
    }

    fun isLoadedAd() = IronSource.isInterstitialReady()

    fun onIronSourceListener(onIronSourceListener: ((SDKIronSourceState) -> Unit)) {
        this.onIronSourceListener = onIronSourceListener
    }

    fun showAd(activity: AppCompatActivity, block: (() -> Unit)) {
        if (!isLoadedAd()) {
            onIronSourceListener?.let { it(SDKIronSourceState.CLOSE) }
            loadAd()
            block()
            return
        }
        onAdListener = block
        onIronSourceListener?.let { it(SDKIronSourceState.SHOW) }
        IronSource.showInterstitial(activity, unitId)
    }


}