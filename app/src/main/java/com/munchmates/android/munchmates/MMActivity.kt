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

        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun load() {
        doAsync {

        }
    }
}