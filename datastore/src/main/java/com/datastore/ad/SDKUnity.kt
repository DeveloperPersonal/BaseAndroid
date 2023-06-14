package com.datastore.ad

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

object SDKUnity {

    private var placementId = ""

    fun initialize(context: Context, gameId: String, block:(()->Unit)) {
        UnityAds.initialize(
            context,
            gameId,
            false,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    block()
                }

                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?, message: String?
                ) {

                }
            })
    }

    fun loadInter(unitId: String, block: (String) -> Unit) {
        placementId = ""
        UnityAds.load(unitId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String?) {
                SDKUnity.placementId = placementId ?: ""
                block("true")
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String?, error: UnityAds.UnityAdsLoadError?, message: String?
            ) {
                block("false: $message")
                SDKUnity.placementId = ""
            }
        })
    }

    fun isInterLoaded() = placementId.isNotEmpty()

    fun showInter(activity: AppCompatActivity, unitId: String, block:(()->Unit)) {
        UnityAds.show(activity, placementId, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String?, error: UnityAds.UnityAdsShowError?, message: String?
            ) {
                SDKUnity.placementId =""
                loadInter(unitId){}
            }

            override fun onUnityAdsShowStart(placementId: String?) {

            }

            override fun onUnityAdsShowClick(placementId: String?) {

            }

            override fun onUnityAdsShowComplete(
                placementId: String?, state: UnityAds.UnityAdsShowCompletionState?
            ) {
                SDKUnity.placementId =""
                loadInter(unitId){}
                block()
            }

        })
        placementId = ""
    }
}