package com.munchmates.android

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.*

class App {

    companion object {
        var current = 0

        var clubs = arrayListOf<Club>()
        var colleges = arrayListOf<CollegeType>()
        var mates = arrayListOf<MateType>()
        var plans = arrayListOf<MPlanType>()
        var user = User()

        fun init(uid: String) {
            FirebaseDatabase.getInstance().reference.child("LISTS/clubsOrgs").addValueEventListener(listener)
            FirebaseDatabase.getInstance().reference.child("LISTS/colleges").addValueEventListener(listener)
            FirebaseDatabase.getInstance().reference.child("LISTS/mateTypes").addValueEventListener(listener)
            FirebaseDatabase.getInstance().reference.child("LISTS/mealPlan").addValueEventListener(listener)
            FirebaseDatabase.getInstance().reference.child("USERS/$uid").addValueEventListener(listener)
            while(current < 5);
        }

        private val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
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
                    user = snapshot.getValue<User>(User::class.java)!!
                }
                current++
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
                current++
            }
        }
    }
}