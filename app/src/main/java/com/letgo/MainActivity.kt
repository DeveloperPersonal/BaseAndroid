package com.letgo

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.datastore.BaseActivity
import com.datastore.job.RepeatingJob
import com.datastore.sdk.SDKAppOpen
import com.datastore.sdk.SDKBanner
import com.letgo.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(), RepeatingJob.RepeatingJobListener {

    private val sdkBanner by lazy {
        SDKBanner(this)
    }

    private val sdkAppOpen by lazy {
        SDKAppOpen(this)
    }

    private val repeatingJob by lazy {
        RepeatingJob()
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateUI() {
        /*sdkBanner.setTryAgainIfError(true)
        sdkBanner.loadAd(binding.frameLayout)
        sdkAppOpen.setNextAdLoad(true)
        sdkAppOpen.setUnitId("").setListener(object : SDKAppOpen.SDKAppOpenListener {
            override fun onAdLoaded() {
                sdkAppOpen.showAd(this@MainActivity)
            }
        })
        sdkAppOpen.loadAd()*/
        startBillingConnection()
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
        Log.i("tinhnv", "onRepeatingJobChange: $progress")
    }
}