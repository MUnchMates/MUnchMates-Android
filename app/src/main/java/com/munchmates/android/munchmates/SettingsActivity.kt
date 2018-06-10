package com.munchmates.android.munchmates

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings_button_logout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings_button_logout -> {
                Prefs.instance.put(Prefs.EMAIL_PREF, "")
                Prefs.instance.put(Prefs.PASSWORD_PREF, "")
                startActivity(Intent(this, MMActivity::class.java))
            }
        }
    }
}