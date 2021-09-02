package com.tokastudio.music_offline

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object SharedPref {
    @JvmStatic
    fun setPrefFav(activity: Activity, trackList_pos: List<Long?>?) {
        val editor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        val gson = Gson()
        editor.putString(Constants.PREF_FAV_KEY, gson.toJson(trackList_pos))
        editor.apply()
    }

    @JvmStatic
    fun getPrefFav(activity: Activity): MutableList<Long> {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        var trackListPos: MutableList<Long>
        val gson = Gson()
        val str = sharedPref.getString(Constants.PREF_FAV_KEY, "")
        trackListPos = (if (str!!.isEmpty()) {
            ArrayList()
        } else {
            val type = object : TypeToken<List<Long?>?>() {}.type
            gson.fromJson<List<Long>>(str, type)
        }) as MutableList<Long>
        return trackListPos
    }

    @JvmStatic
    fun setPref(activity: Activity, key: String?, value: Any) {
        val editor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> {
                editor.putString(key, value.toString())
            }
            is Int -> {
                editor.putInt(key, value.toString().toInt())
            }
            is Boolean -> {
                editor.putBoolean(key, java.lang.Boolean.parseBoolean(value.toString()))
            }
        }
        editor.apply()
    }

    @JvmStatic
    fun getStringPref(activity: Activity, key: String?): String? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    fun getIntPref(activity: Activity, key: String?): Int {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getInt(key, 0)
    }

    fun getBooleanPref(activity: Activity, key: String?): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }
}