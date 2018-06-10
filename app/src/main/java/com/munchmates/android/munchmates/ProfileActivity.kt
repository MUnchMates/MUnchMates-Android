package com.munchmates.android.munchmates

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val name: TextView = findViewById(R.id.profile_text_name)
        val town: TextView = findViewById(R.id.profile_text_town)
        val type: TextView = findViewById(R.id.profile_text_type)
        val college: TextView = findViewById(R.id.profile_text_college)
        val image: ImageView = findViewById(R.id.profile_image_avatar)
        val clubs: LinearLayout = findViewById(R.id.profile_list_clubs)

        name.text = "Place Holder"
        town.text = "Placeholder, USA"
        type.text = "Freshman"
        college.text = "Arts and Sciences"
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