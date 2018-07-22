package com.munchmates.android.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.Prefs
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button_login.setOnClickListener(this)
        login_button_create.setOnClickListener(this)
        title = "MunchMates Login"

        val email = Prefs.instance.getStr(Prefs.EMAIL_PREF)
        val password = Prefs.instance.getStr(Prefs.PASSWORD_PREF)
        if(email != "" && password != "") {
            login_edit_email.setText(email)
            login_edit_password.setText(password)
            login(email, password)
        }
        checkGPS()
    }

    fun checkGPS() {
        val gps = GoogleApiAvailability.getInstance()
        val resultCode = gps.isGooglePlayServicesAvailable(this)
        if(resultCode != ConnectionResult.SUCCESS) {
            error("MUnchMates requires an up-to-date version of Google Play Services.")
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
            if(email.endsWith("@marquette.edu", true) ||  email.endsWith("@mu.edu", true)) {
                when (v?.id) {
                    R.id.login_button_login -> {
                        login(email, password)
                    }
                    R.id.login_button_create -> {
                        if(password.length > 5) {
                            createAccount(email, password)
                        }
                        else {
                            error("Password must be at least 6 characters long.")
                        }
                    }
                }
            }
            else {
                login_text_error.text = "App requires a Marquette email address"
                login_text_error.visibility = View.VISIBLE
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful) {
                val user = User()
                user.uid = auth.uid!!
                val names = email.split('@')[0].split('.')
                user.firstName = names[0]
                user.lastName = names.last()
                user.email = email
                user.searchOrderNumber = Random().nextInt((10000 + 1) - 1) + 1
                user.lastOpened = SimpleDateFormat("M.d.yyyy • H:mm:ss").format(Date())
                FirebaseDatabase.getInstance().reference.child("USERS/${auth.currentUser!!.uid}").setValue(user)
                success(auth.currentUser!!)
            }
            else {
                failure(task.exception!!)
            }
        }
    }

    private fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful) {
                success(auth.currentUser!!)
            }
            else {
                failure(task.exception!!)
            }
        }
    }

    private fun success(user: FirebaseUser) {
        toast("Welcome ${user!!.email}!")
        val c = this
        doAsync {
            App.init(user.uid, c)
            startActivity(Intent(c, HomeActivity::class.java))
        }
    }

    private fun failure(taskEx: Exception) {
        Prefs.instance.put(Prefs.EMAIL_PREF, "")
        Prefs.instance.put(Prefs.PASSWORD_PREF, "")

        val ex = taskEx.toString()
        var userEx = ex.substring(ex.lastIndexOf(": ")+2)
        error(userEx)
    }

    private fun error(error: String) {
        login_text_error.text = error
        login_text_error.visibility = View.VISIBLE
    }
}