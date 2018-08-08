package com.munchmates.android.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.Club
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_list.*

class ClubActivity : BaseMMActivity(), CompoundButton.OnCheckedChangeListener {
    val results = hashMapOf<Club, Switch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        title = "Select Clubs"
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        list_list_list.removeAllViews()
        for(club in App.clubs) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_club_enable, list_list_list as ViewGroup, false)

            view.findViewById<TextView>(R.id.club_text_name).text = club.clubsOrgsName

            list_list_list.addView(view)
            list_list_list.addView(LayoutInflater.from(this).inflate(R.layout.spacer, list_list_list as ViewGroup, false))

            val switch = view.findViewById<Switch>(R.id.club_switch_enable)
            if(App.user.clubsOrgs.containsValue(club)) switch.isChecked = true
            switch.setOnCheckedChangeListener(this)

            results[club] = switch
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        for(club in results.keys) {
            if(results.getValue(club) == buttonView) {
                if(isChecked) {
                    App.user.clubsOrgs[club.clubsOrgsId] = club
                }
                else {
                    App.user.clubsOrgs.remove(club.clubsOrgsId)
                }
                FirebaseDatabase.getInstance().reference.child("USERS/${App.user.uid}/").setValue(App.user)
            }
        }
    }
}