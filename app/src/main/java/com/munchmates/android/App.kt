package com.munchmates.android

import com.google.firebase.auth.FirebaseAuth
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

        fun init() {
            FirebaseDatabase.getInstance().reference.child("LISTS/clubsOrgs").addValueEventListener(TypeListener())
            FirebaseDatabase.getInstance().reference.child("LISTS/colleges").addValueEventListener(TypeListener())
            FirebaseDatabase.getInstance().reference.child("LISTS/mateTypes").addValueEventListener(TypeListener())
            FirebaseDatabase.getInstance().reference.child("LISTS/mealPlan").addValueEventListener(TypeListener())
            FirebaseDatabase.getInstance().reference.child("USERS/${FirebaseAuth.getInstance().uid}").addValueEventListener(TypeListener())
            while(current < 5);
        }
    }

    private class TypeListener: ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.ref.toString().contains("clubsOrgs")) {
                for(type in snapshot.children) clubs.add(type.getValue<Club>(Club::class.java)!!)
            }
            if(snapshot.ref.toString().contains("colleges")) {
                for(type in snapshot.children) colleges.add(type.getValue<CollegeType>(CollegeType::class.java)!!)
            }
            if(snapshot.ref.toString().contains("mateTypes")) {
                for(type in snapshot.children) mates.add(type.getValue<MateType>(MateType::class.java)!!)
            }
            if(snapshot.ref.toString().contains("mealPlan")) {
                for(type in snapshot.children) plans.add(type.getValue<MPlanType>(MPlanType::class.java)!!)
            }
            if(snapshot.ref.toString().contains("USERS")) {
                user = snapshot.getValue<User>(User::class.java)!!
            }
            current++
        }

        override fun onCancelled(error: DatabaseError) {}
    }
}