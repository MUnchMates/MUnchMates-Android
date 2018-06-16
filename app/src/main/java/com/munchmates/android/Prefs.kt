package com.munchmates.android

import android.content.Context
import android.content.SharedPreferences

class Prefs(c: Context) {

    private val PREF_TITLE = "MM_PREFS"
    private val prefs: SharedPreferences

    init {
        prefs = c.getSharedPreferences(PREF_TITLE, 0)
    }

    companion object {
        val EMAIL_PREF = arrayOf("EMAIL_PREF", "")
        val PASSWORD_PREF = arrayOf("PASSWORD_PREF", "")

        lateinit var instance: Prefs

        fun setInstance(c: Context): Prefs {
            instance = Prefs(c)
            return instance
        }
    }

    fun getStr(pref: Array<String>): String {
        return prefs.getString(pref[0], pref[1])
    }

    fun getBool(pref: Array<String>): Boolean {
        return prefs.getBoolean(pref[0], pref[1].toBoolean())
    }

    fun getInt(pref: Array<String>): Int {
        return prefs.getInt(pref[0], pref[1].toInt())
    }

    fun put(pref: Array<String>, value: Any): Boolean {
        var edit = prefs.edit()
        when(value) {
            is String -> edit.putString(pref[0], value)
            is Int -> edit.putInt(pref[0], value)
            is Boolean -> edit.putBoolean(pref[0], value)
            else -> {
                println("Unknown type passed")
                return false
            }
        }
        edit.commit()
        return true
    }
}
