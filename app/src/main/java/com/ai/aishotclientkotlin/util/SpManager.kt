package com.ai.aishotclientkotlin.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SpManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun setSharedPreference( key: Sp, value: String?) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.putString(key.toString(), value)
        edit.commit()
    }


    // TODO: this a stick way to solve multiple value json ?,pls fix it in future.
    fun getThenSetSharedPreference( key: Sp, value: String?) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val oldValue = sharedPref.getString(key.toString(),null)
        if(value !== null&& value != oldValue) {
            val edit = sharedPref.edit()
            edit.putString(key.toString(), value)
            edit.commit()
        }
    }

    fun getSharedPreference( key: Sp, defaultValue: String?): String? {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(key.toString(), defaultValue)
    }

    fun clearSharedPreference() {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.clear()
        edit.commit()
    }

    fun removeSharedPreference( key: Sp) {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.remove(key.toString())
        edit.commit()
    }

    enum class Sp {
        USERNAME,
        PASSWORD,
        USERID,
        THEME,
        BLE,
        JWT_TOKEN,
        REFRESH_TOKEN,
    }

}