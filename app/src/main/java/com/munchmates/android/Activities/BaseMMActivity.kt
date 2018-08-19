package com.munchmates.android.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.munchmates.android.App
import com.munchmates.android.Prefs

abstract class BaseMMActivity: AppCompatActivity() {

    override fun onResume() {
        if(Prefs.instance != null) {
            Prefs.setInstance(this)
        }
        if(App.plans.isEmpty()) {
            val intent = Intent(this, MMActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        super.onResume()
    }
}