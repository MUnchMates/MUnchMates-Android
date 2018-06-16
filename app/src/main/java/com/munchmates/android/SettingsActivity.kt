package com.munchmates.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener, ValueEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings_button_logout.setOnClickListener(this)

        fillPage()
    }

    private fun fillPage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val usersRef = FirebaseDatabase.getInstance().reference
        usersRef.child("USERS/$uid").addValueEventListener(this)
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onDataChange(snapshot: DataSnapshot) {
        val user = snapshot.getValue<User>(User::class.java)

        settings_edit_first.setText(user!!.firstName)
        settings_edit_last.setText(user!!.lastName)
        settings_edit_town.setText(user!!.city)
        settings_edit_state.setText(user!!.stateCountry)

        settings_switch_mute.isChecked = user!!.muteMode
        settings_switch_meal.isChecked = user!!.mealPlan
        settings_switch_notif.isChecked = user!!.emailNotifications
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings_button_logout -> {
                Prefs.instance.put(Prefs.EMAIL_PREF, "")
                Prefs.instance.put(Prefs.PASSWORD_PREF, "")
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MMActivity::class.java))
            }
        }
    }
}