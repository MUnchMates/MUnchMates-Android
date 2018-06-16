package com.munchmates.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), ValueEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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

        profile_text_name.text = " ${user!!.firstName} ${user!!.lastName}"
        profile_text_town.text = " ${user!!.city}, ${user!!.stateCountry}"
        profile_text_type.text = " ${user!!.mateType}"
        profile_text_college.text = " ${user!!.college}"

        for(club in user!!.clubsOrgs.values) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_org_list, profile_list_clubs, false) as TextView
            view.text = club.clubsOrgsName
            profile_list_clubs.addView(view)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}