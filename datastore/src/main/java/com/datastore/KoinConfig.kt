package com.datastore

import android.app.Activity
import androidx.activity.ComponentActivity
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.scope.LifecycleScopeDelegate
import org.koin.androidx.scope.activityScope
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

private fun getOrCreateKoin(activity: ComponentActivity): Koin {
    return if (activity is KoinComponent) {
        activity.getKoin()
    } else {
        GlobalContext.getOrNull() ?: startKoin {
            androidContext(activity.applicationContext)
            modules(koinModules)
        }.koin
    }
}

fun ComponentActivity.createScope() = runCatching {
    LifecycleScopeDelegate<Activity>(
        lifecycleOwner = this,
        koin = getOrCreateKoin(this)
    )
}.getOrElse { activityScope() }