package com.munchmates.android.munchmates

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_button_login -> {
                val email = login_edit_email.text
                val password = login_edit_password.text
                Toast.makeText(this, "$email :: $password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}