package com.datastore.sdk

import com.google.android.gms.ads.AdRequest

object SDKConfig {

    /*
* <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
* */

    const val ID_UNIT_APP_ADMOB_TEST = "ca-app-pub-3940256099942544~3347511713"
    const val ID_UNIT_BANNER_ADMOB_TEST = "ca-app-pub-3940256099942544/6300978111"
    const val ID_UNIT_APP_OPEN_TEST = "ca-app-pub-3940256099942544/3419835294"

    fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

}