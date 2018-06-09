package com.munchmates.android.munchmates

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.doAsync

class MMActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mm)
        Prefs.setInstance(this)
        load()
    }

    fun load() {
        val c = this
        doAsync {
            if(Prefs.instance.getStr(Prefs.EMAIL_PREF) != "" && Prefs.instance.getStr(Prefs.PASSWORD_PREF) != "") {
                //attempt login
                startActivity(Intent(c, HomeActivity::class.java))
                return@doAsync
            }
            startActivity(Intent(c, LoginActivity::class.java))
        }
    }
}