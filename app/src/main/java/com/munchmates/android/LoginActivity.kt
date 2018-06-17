package com.munchmates.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button_login.setOnClickListener(this)
        login_button_create.setOnClickListener(this)

        val email = Prefs.instance.getStr(Prefs.EMAIL_PREF)
        val password = Prefs.instance.getStr(Prefs.PASSWORD_PREF)
        if(email != "" && password != "") {
            login_edit_email.setText(email)
            login_edit_password.setText(password)
            login(email, password)
        }
    }

    override fun onClick(v: View?) {
        login_text_error.visibility = View.INVISIBLE
        val email = login_edit_email.text.toString()
        val password = login_edit_password.text.toString()
        // manual remember me
        //Prefs.instance.put(Prefs.EMAIL_PREF, email)
        //Prefs.instance.put(Prefs.PASSWORD_PREF, password)
        if(email.isNotEmpty() && password.isNotEmpty()) {
            when(v?.id) {
                R.id.login_button_login -> {
                    login(email, password)
                }
                R.id.login_button_create -> {
                    createAccount(email, password)
                }
            }
        }
        else {
            login_text_error.text = "No username or password provided."
            login_text_error.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun createAccount(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful) {
                success(auth.currentUser!!)
            }
            else {
                failure(task.exception!!)
            }
        })
    }

    private fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful) {
                success(auth.currentUser!!)
            }
            else {
                failure(task.exception!!)
            }
        })
    }

    private fun success(user: FirebaseUser) {
        toast("Welcome ${user!!.email}!")
        App.init()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun failure(taskEx: Exception) {
        Prefs.instance.put(Prefs.EMAIL_PREF, "")
        Prefs.instance.put(Prefs.PASSWORD_PREF, "")

        val ex = taskEx.toString()
        var userEx = ex.substring(ex.lastIndexOf(": ")+2)
        login_text_error.text = userEx
        login_text_error.visibility = View.VISIBLE
    }
}