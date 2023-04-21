package com.letgo

import androidx.lifecycle.lifecycleScope
import com.datastore.BaseActivity
import com.datastore.DebugLog
import com.datastore.ad.AdLoadOder
import com.datastore.ad.AdPriority
import com.datastore.job.RepeatingJob
import com.datastore.ad.SDKAppOpen
import com.datastore.ad.SDKBanner
import com.datastore.ad.SDKRewarded
import com.datastore.ad.SDKRewardedLoss
import com.letgo.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(), RepeatingJob.RepeatingJobListener,
    SDKRewarded.SDKRewardedListener {

    private val sdkBanner by lazy {
        SDKBanner(this)
    }

    private val sdkAppOpen by lazy {
        SDKAppOpen(this)
    }

    private val repeatingJob by lazy {
        RepeatingJob()
    }

    private val sdkRewardedLoss by lazy {
        SDKRewardedLoss(this)
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateUI() {/*sdkBanner.setTryAgainIfError(true)
        sdkBanner.loadAd(binding.frameLayout)
        sdkAppOpen.setNextAdLoad(true)
        sdkAppOpen.setUnitId("").setListener(object : SDKAppOpen.SDKAppOpenListener {
            override fun onAdLoaded() {
                sdkAppOpen.showAd(this@MainActivity)
            }
        })
        sdkAppOpen.loadAd()*/
        sdkRewardedLoss.setAdLoadOder(AdLoadOder.PARALLEL)
        sdkRewardedLoss.setAdUnitId("ca-app-pub-3940256099942544/5224354917", "")
        sdkRewardedLoss.setListener(this)
        sdkRewardedLoss.loadAd()
    }

    override fun onResume() {
        repeatingJob.runJob(lifecycleScope, this)
        super.onResume()
    }

    override fun onPause() {
        repeatingJob.cancelJob()
        super.onPause()
    }

    override fun onRepeatingJobChange(progress: Int) {

    }

    override fun onAdDismissedFullScreenContent(isRewardedItem: Boolean) {
        DebugLog.debugLog("isRewardedItem: $isRewardedItem")
    }

    override fun onAdLoaded() {
        sdkRewardedLoss.showAd()
    }

    override fun onAdFailedToLoad(msg: String) {
        DebugLog.debugLog(msg)
    }
}