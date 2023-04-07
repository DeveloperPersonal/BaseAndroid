package com.datastore

import android.content.Context
import android.content.SharedPreferences

private val Context.pref: SharedPreferences
    get() = getSharedPreferences(
        packageName,
        Context.MODE_PRIVATE
    )

private val Context.editor: SharedPreferences.Editor
    get() = pref.edit()

fun Context.setIntPref(key: String, value: Int) {
    editor.putInt(key, value).apply()
}

fun Context.getIntPref(key: String, defaultValue: Int = -1): Int {
    return pref.getInt(key, defaultValue)
}

fun Context.getStringPref(key: String, defaultValue: String = ""): String {
    return pref.getString(key, defaultValue).toString()
}

fun Context.getBooleanPref(key: String, defaultValue: Boolean = false): Boolean {
    return pref.getBoolean(key, defaultValue)
}

fun Context.setBooleanPref(key: String, value: Boolean) {
    editor.putBoolean(key, value).apply()
}

fun Context.setStringPref(key: String, value: String) {
    editor.putString(key, value).apply()

}