package com.datastore

import com.datastore.billing.SDKBillingV4
import org.koin.core.module.Module
import org.koin.dsl.module

private val defaultModule = module {
    scope<BaseActivity<*>> {
        factory { SDKBillingV4(get()) }
    }
}

val koinModules = mutableListOf<Module>().apply {
    add(defaultModule)
}