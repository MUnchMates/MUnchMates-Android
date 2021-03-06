package com.munchmates.android.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.munchmates.android.App
import com.munchmates.android.R
import com.munchmates.android.Utils
import com.munchmates.android.VerifyDialog
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseMMActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        home_button_search.setOnClickListener(this)
        home_button_messages.setOnClickListener(this)

        val tAdapter = ArrayAdapter(this, R.layout.item_link, resources.getStringArray(R.array.groups))
        tAdapter.setDropDownViewResource(R.layout.item_spinner)
        home_spinner_type.adapter = tAdapter
        home_spinner_type.onItemSelectedListener = this

        val gAdapter = ArrayAdapter(this, R.layout.item_link, arrayOf("all"))
        gAdapter.setDropDownViewResource(R.layout.item_spinner)
        home_spinner_group.adapter = gAdapter
        //home_spinner_group.onItemSelectedListener = this

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("USERS/$uid/lastOpened").setValue(Utils.getDate(Utils.userFormat))
        println("User: $uid")
    }

    override fun onResume() {
        super.onResume()
        var read = 0
        for(sender in App.user.conversations.senderList.values) {
            if(!sender.read) {
                read++
            }
        }
        if(read > 0) {
            home_button_messages.text = "Messages ($read)"
        }
        else {
            home_button_messages.text = "Messages"
        }

        checkVerification(0)
    }

    fun checkVerification(last: Long) {
        if(!FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
            val dialog = VerifyDialog()
            val args = Bundle()
            args.putLong("last", last)
            dialog.arguments = args
            dialog.show(fragmentManager.beginTransaction(), "dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        home_text_whatgroup.text = "Which ${resources.getStringArray(R.array.groups)[home_spinner_type.selectedItemPosition]}?"
        val names = arrayListOf<String>()
        when(home_spinner_type.selectedItemPosition) {
            0 -> { // club
                for (club in App.clubs) {
                    names.add(club.clubsOrgsName)
                }
            }
            1 -> { // college
                for (college in App.colleges) {
                    names.add(college.collegeName)
                }
            }
            2 -> { // mate type
                for (mate in App.mates) {
                    names.add(mate.mateTypeName)
                }
            }
            3 -> { //meal plan
                for (plan in App.plans) {
                    names.add(plan.mealPlanName)
                }
            }
            4 -> { //home town
                names.addAll(App.towns)
            }
            5 -> { //state / country
                names.addAll(App.states)
            }
        }
        names.sort()
        val gAdapter = ArrayAdapter(this, R.layout.item_link, names)
        gAdapter.setDropDownViewResource(R.layout.item_spinner)
        home_spinner_group.adapter = gAdapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.home_button_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("type", home_spinner_type.selectedItemPosition)
                intent.putExtra("group", home_spinner_group.selectedItem as String)
                startActivity(intent)
            }
            R.id.home_button_messages -> {
                startActivity(Intent(this, MessageActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}