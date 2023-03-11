package com.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun BaseActivity<*>.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        block()
    }
}

fun BaseActivity<*>.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch(Dispatchers.IO) {
        block()
    }
}

fun ViewModel.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch {
        block()
    }
}

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(Dispatchers.IO) {
        block()
    }
}

fun coroutineLaunchMain(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
        block()
    }
}

fun coroutineLaunchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        block()
    }
}