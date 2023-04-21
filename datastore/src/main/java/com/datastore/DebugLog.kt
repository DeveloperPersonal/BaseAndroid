package com.datastore

import android.util.Log

object DebugLog {

    private var debug = true

    fun stateLog(debug: Boolean) {
        DebugLog.debug = debug
    }

    fun debugLog(any: Any?) {
        if (!debug) return
        Log.i("BaseAndroid", "debugLog: $any")
    }
}