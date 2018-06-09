package com.munchmates.android.munchmates

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button_login.setOnClickListener(this)

        if(Prefs.instance.getStr(Prefs.EMAIL_PREF) != "" && Prefs.instance.getStr(Prefs.PASSWORD_PREF) != "")
        {
            login_edit_email.setText(Prefs.instance.getStr(Prefs.EMAIL_PREF))
            login_edit_password.setText(Prefs.instance.getStr(Prefs.PASSWORD_PREF))
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_button_login -> {
                val email = login_edit_email.text
                val password = login_edit_password.text
                toast("$email :: $password")
                Prefs.instance.put(Prefs.EMAIL_PREF, email.toString())
                Prefs.instance.put(Prefs.PASSWORD_PREF, password.toString())
                if(true)
                {
                    // successful login
                    toast("Welcome ${Prefs.instance.getStr(Prefs.EMAIL_PREF)}!")
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }
    }
}