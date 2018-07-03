package com.munchmates.android.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.munchmates.android.App
import com.munchmates.android.Prefs
import com.munchmates.android.R
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
            if(FirebaseAuth.getInstance().currentUser == null) {
                // requires login
                startActivity(Intent(c, LoginActivity::class.java))
            }
            else {
                App.init()
                // enter app
                startActivity(Intent(c, HomeActivity::class.java))
            }
        }
    }
}