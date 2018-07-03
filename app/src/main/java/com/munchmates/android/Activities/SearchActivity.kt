package com.munchmates.android.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import com.munchmates.android.DatabaseObjs.*
import com.munchmates.android.Firebase.LoadingDialog
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_list.*

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    val dialog = LoadingDialog(::respond)
    var results = hashMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val type = intent.getIntExtra("type", 0)
        val group = intent.getStringExtra("group")
        println("Searching for a ${resources.getStringArray(R.array.groups)[type]} called $group")

        getResults(type, group)
    }

    private fun getResults(type: Int, group: String) {
        dialog.show(fragmentManager.beginTransaction(), "dialog")

        val usersRef = FirebaseDatabase.getInstance().reference.child("USERS")
        when(type) {
            0 -> { // club
                usersRef.addValueEventListener(dialog)
            }
            1 -> { // college
                usersRef.orderByChild("college").equalTo(group).addValueEventListener(dialog)
            }
            2 -> { // mate type
                usersRef.orderByChild("mateType").equalTo(group).addValueEventListener(dialog)
            }
            3 -> { //meal plan
                usersRef.orderByChild("mealPlan").equalTo(group == "Yes").addValueEventListener(dialog)
            }
        }
    }

    fun respond(snapshot: DataSnapshot) {
        val users = arrayListOf<User>()
        println("Number of results ${snapshot.childrenCount}")
        for(user in snapshot.children) {
            try {
                val user = user.getValue<User>(User::class.java)!!
                if(intent.getIntExtra("type", 0) == 0) {
                    for(club in user.clubsOrgs.values) {
                        if(club.clubsOrgsName == intent.getStringExtra("group")) users.add(user)
                    }
                }
                else {
                    users.add(user)
                }
            } catch (e: DatabaseException) {
                println("Bad value on new user:")
                println(user.value)
            }
        }

        users.shuffle()
        results = hashMapOf()
        list_list_list.removeAllViews()
        for(user in users) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_search_result, list_list_list as ViewGroup, false)

            view.findViewById<TextView>(R.id.result_text_name).text = "${user.firstName} ${user.lastName}"
            view.findViewById<TextView>(R.id.result_text_college).text = "${user.college}"
            view.findViewById<TextView>(R.id.result_text_class).text = "${user.mateType}"

            if(user.mealPlan) {
                view.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            }
            list_list_list.addView(view)
            list_list_list.addView(LayoutInflater.from(this).inflate(R.layout.spacer, list_list_list as ViewGroup, false))
            results.put(user.uid, view)
            view.setOnClickListener(this)
        }

        dialog.dismiss()
    }

    override fun onClick(v: View?) {
        for(user in results.keys) {
            if(results.getValue(user) == v) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("uid", user)
                startActivity(intent)
            }
        }
    }
}