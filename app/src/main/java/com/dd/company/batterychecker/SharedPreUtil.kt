package com.dd.company.batterychecker

import android.content.Context
import android.content.SharedPreferences


class SharedPreUtil(context: Context) {

    companion object {
        const val PREF_NAME = "PREF_NAME"
        var mPrefUtils: SharedPreUtil? = null

        fun getInstance(context: Context): SharedPreUtil {
            return mPrefUtils ?: SharedPreUtil(context)
        }
    }

    var mPreferences: SharedPreferences? = null

    init {
        mPreferences =
            context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putValue(key: String, value: Any) {
        val editor = mPreferences?.edit()
        when (value) {
            is Int -> editor?.putInt(key, value)
            is String -> editor?.putString(key, value)
            is Float -> editor?.putFloat(key, value)
            is Long -> editor?.putLong(key, value)
            is Boolean -> editor?.putBoolean(key, value)
        }
        editor?.commit()
    }

    fun getValue(key: String, defaultValue: Int): Int {
        return mPreferences?.getInt(key, defaultValue) ?: defaultValue
    }

    fun getValue(key: String, defaultValue: String): String {
        return mPreferences?.getString(key, defaultValue) ?: defaultValue
    }

    fun getValue(key: String, defaultValue: Long): Long {
        return mPreferences?.getLong(key, defaultValue) ?: defaultValue
    }

    fun getValue(key: String, defaultValue: Float): Float {
        return mPreferences?.getFloat(key, defaultValue) ?: defaultValue
    }

    fun getValue(key: String, defaultValue: Boolean): Boolean {
        return mPreferences?.getBoolean(key, defaultValue) ?: defaultValue
    }
}
