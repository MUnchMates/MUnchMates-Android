package com.munchmates.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener, ValueEventListener {

    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        uid = intent.getStringExtra("uid")
        println("UID: $uid")
        if(uid != FirebaseAuth.getInstance().currentUser?.uid) {
            profile_button_add.visibility = View.GONE
        }
        profile_button_add.setOnClickListener(this)

        fillPage()
    }

    private fun fillPage() {
        val userRef = FirebaseDatabase.getInstance().reference
        userRef.child("USERS/$uid").addValueEventListener(this)

        val storage = FirebaseStorage.getInstance()
        val stoRef = storage.getReferenceFromUrl("gs://munch-mates-marquette.appspot.com/imgProfilePictures/").child("$uid.png")
        Glide.with(this)
                .load(stoRef)
                .into(profile_image_avatar)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val user = snapshot.getValue<User>(User::class.java)!!

        profile_text_name.text = "${user.firstName} ${user.lastName}"
        if(user.city == "" || user.stateCountry == "") {
            profile_text_town.text = "${user.city}${user.stateCountry}"
        }
        if(user.city == "" && user.stateCountry == "") {
            profile_text_town.visibility = View.GONE
        }
        else {
            profile_text_town.text = "${user.city}, ${user.stateCountry}"
        }
        profile_text_type.text = " ${user.mateType}"
        profile_text_college.text = " ${user.college}"

        for(club in user.clubsOrgs.values) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_org_list, profile_list_clubs as ViewGroup, false) as TextView
            view.text = "- ${club.clubsOrgsName}"
            profile_list_clubs.addView(view)
        }
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.profile_button_add -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(uid == FirebaseAuth.getInstance().currentUser?.uid) {
            menuInflater.inflate(R.menu.profile, menu)
        }
        else {
            menuInflater.inflate(R.menu.user, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.action_message -> {
                val intent = Intent(this, ConversationActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}