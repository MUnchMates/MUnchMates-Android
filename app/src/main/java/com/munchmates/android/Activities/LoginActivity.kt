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
import com.munchmates.android.Utils
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
        login_button_forgot.setOnClickListener(this)
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
        if(email.isNotEmpty() && (password.isNotEmpty() || v?.id == R.id.login_button_forgot)) {
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
                    R.id.login_button_forgot -> {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        error("Password reset requested.")
                    }
                }
            }
            else {
                error("App requires a Marquette email address.")
            }
        }
        else {
            error("No username or password provided.")
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
                user.firstName = names[0].substring(0, 1).toUpperCase() + names[0].substring(1).toLowerCase()
                user.lastName = names.last().substring(0, 1).toUpperCase() + names.last().substring(1).toLowerCase()
                user.email = email
                user.college = "No College Affiliation"
                user.mateType = "Other"
                user.searchOrderNumber = Random().nextInt((10000 + 1) - 1) + 1
                user.lastOpened = Utils.getDate(Utils.userFormat)
                FirebaseDatabase.getInstance().reference.child("USERS/${auth.currentUser!!.uid}").setValue(user)
                FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                success(auth.currentUser!!, true)
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
                success(auth.currentUser!!, false)
            }
            else {
                failure(task.exception!!)
            }
        }
    }

    private fun success(user: FirebaseUser, newAcct: Boolean) {
        toast("Welcome ${user!!.email}!")
        val c = this
        doAsync {
            App.init(user.uid, c)
            if(newAcct) {
                val intent = Intent(c, SettingsActivity::class.java)
                intent.putExtra("new", true)
                startActivity(intent)
            }
            else {
                startActivity(Intent(c, HomeActivity::class.java))
            }
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