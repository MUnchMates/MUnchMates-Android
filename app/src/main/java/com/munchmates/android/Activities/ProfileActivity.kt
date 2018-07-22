package com.munchmates.android.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.Firebase.LoadingDialog
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    var uid = ""
    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profile_button_add.setOnClickListener(this)

        uid = intent.getStringExtra("uid")
        user = App.users[uid]!!
        println("UID: $uid")

        if(uid != FirebaseAuth.getInstance().currentUser?.uid) {
            profile_button_add.visibility = View.GONE
            title = "${user.firstName} ${user.lastName}"
        }
        else {
            title = "Your Profile"
        }

        fillPage()
    }

    override fun onResume() {
        super.onResume()

        //fillPage(App.users[uid]!!)
    }

    private fun fillPage() {
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

        if(user.mealPlan) {
            profile_text_mealplan.text = " unlimited"
        }
        else {
            profile_text_mealplan.text = " none"
        }

        profile_list_clubs.removeAllViews()
        for(club in user.clubsOrgs.values) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_org_list, profile_list_clubs as ViewGroup, false) as LinearLayout
            view.findViewById<TextView>(R.id.club_text_name).text = "${club.clubsOrgsName}"
            view.setOnClickListener(ClubItem(club.clubsOrgsName, this))
            profile_list_clubs.addView(view)
        }
        profile_list_clubs.addView(LayoutInflater.from(this).inflate(R.layout.spacer, profile_list_clubs as ViewGroup, false))

        val storage = FirebaseStorage.getInstance()
        val stoRef = storage.getReferenceFromUrl("gs://munch-mates-marquette.appspot.com/imgProfilePictures/").child("$uid.png")

        val ops = RequestOptions()
                .placeholder(R.drawable.default_head)
                .error(R.drawable.default_head)

        Glide.with(this)
                .load(stoRef)
                .apply(ops)
                .into(profile_image_avatar)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.profile_button_add -> {
                val intent = Intent(this, ClubActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
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
                intent.putExtra("name", "${user.firstName} ${user.lastName}")
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    class ClubItem: View.OnClickListener {
        var name: String
        var c: Activity

        constructor(clubName: String, activity: Activity) {
            name = clubName
            c = activity
        }
        override fun onClick(v: View?) {
            val intent = Intent(c, SearchActivity::class.java)
            intent.putExtra("type", 0)
            intent.putExtra("group", name)
            c.startActivity(intent)
        }

    }
}