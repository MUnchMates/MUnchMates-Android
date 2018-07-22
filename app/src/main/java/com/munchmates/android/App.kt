package com.munchmates.android

import android.app.Activity
import com.google.firebase.database.*
import com.munchmates.android.DatabaseObjs.*
import com.munchmates.android.Firebase.LoadingDialog

class App {

    companion object {
        var current = 0

        var clubs = arrayListOf<Club>()
        var colleges = arrayListOf<CollegeType>()
        var mates = arrayListOf<MateType>()
        var plans = arrayListOf<MPlanType>()
        var users = hashMapOf<String, User>()
        var user = User()

        var searches = hashMapOf<String, ArrayList<User>>()

        fun init(uid: String, c: Activity) {
            val dialog = LoadingDialog(::respond)
            dialog.show(c.fragmentManager.beginTransaction(), "dialog")
            FirebaseDatabase.getInstance().reference.child("LISTS/clubsOrgs").addValueEventListener(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/colleges").addValueEventListener(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/mateTypes").addValueEventListener(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/mealPlan").addValueEventListener(dialog)
            FirebaseDatabase.getInstance().reference.child("USERS/").addValueEventListener(dialog)
            while(current < 5);
            user = users[uid]!!
            dialog.dismiss()
        }

        private fun respond(snapshot: DataSnapshot) {
            val refStr = snapshot.ref.toString()
            println("Loading $refStr")
            if(refStr.contains("clubsOrgs")) {
                for(type in snapshot.children) clubs.add(type.getValue<Club>(Club::class.java)!!)
            }
            else if(refStr.contains("colleges")) {
                for(type in snapshot.children) colleges.add(type.getValue<CollegeType>(CollegeType::class.java)!!)
            }
            else if(refStr.contains("mateTypes")) {
                for(type in snapshot.children) mates.add(type.getValue<MateType>(MateType::class.java)!!)
            }
            else if(refStr.contains("mealPlan")) {
                for(type in snapshot.children) plans.add(type.getValue<MPlanType>(MPlanType::class.java)!!)
            }
            else if(refStr.contains("USERS")) {
                for(type in snapshot.children) {
                    try {
                        val user = type.getValue<User>(User::class.java)!!
                        users[user.uid] = user
                    } catch (e: DatabaseException) {
                        println("Bad value on new user:")
                        println(type.value)
                    }
                }
            }
            current++
        }
    }
}