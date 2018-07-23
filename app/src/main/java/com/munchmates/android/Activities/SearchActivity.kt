package com.munchmates.android.Activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.*
import com.munchmates.android.Firebase.LoadingDialog
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_list.*

class SearchActivity : BaseMMActivity(), View.OnClickListener {
    val dialog = LoadingDialog(::respond)
    var results = hashMapOf<String, View>()
    var type = 0
    var group = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        type = intent.getIntExtra("type", 0)
        group = intent.getStringExtra("group")

        println("Searching for a ${resources.getStringArray(R.array.groups)[type]} called $group")
        title = "$group Mates"

        dialog.show(fragmentManager.beginTransaction(), "dialog")
        if(App.searches.containsKey("$type:$group")) {
            displayResults(App.searches["$type:$group"]!!)
        }
        else {
            getResults(type, group)
        }
    }

    private fun getResults(type: Int, group: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("USERS")
        when(type) {
            0 -> { // club
                val users = arrayListOf<User>()
                for (user in App.users.values) {
                    for (club in user.clubsOrgs.values) {
                        if (club.clubsOrgsName == intent.getStringExtra("group")) users.add(user)
                    }
                }
                App.searches["$type:$group"] = users
                displayResults(users)
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

    private fun respond(snapshot: DataSnapshot) {
        val users = arrayListOf<User>()
        println("Number of results ${snapshot.childrenCount}")
        for (user in snapshot.children) {
            try {
                val user = user.getValue<User>(User::class.java)!!
                if (intent.getIntExtra("type", 0) == 0) {
                } else {
                    users.add(user)
                }
            } catch (e: DatabaseException) {
                println("Bad value on new user:")
                println(user.value)
            }
        }
        App.searches["$type:$group"] = users
        displayResults(users)
    }

    private fun displayResults(users: ArrayList<User>) {
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
            results[user.uid] = view
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