package com.datastore

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.datastore.billing.Billing
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidContext(applicationContext)
            koinModules.add(defaultModule)
            koinModules.addAll(addKoinModules())
            modules(koinModules)
        }
        val builder = RequestConfiguration.Builder()
        builder.setTestDeviceIds(addDeviceTest())
        MobileAds.setRequestConfiguration(builder.build())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    open fun addKoinModules(): MutableList<Module> {
        return mutableListOf()
    }

    open fun addDeviceTest(): MutableList<String> {
        return mutableListOf()
    }

    open fun addBillings(): MutableList<Billing> {
        return mutableListOf()
    }

    open fun isPremium(): Boolean {
        return false
    }
}