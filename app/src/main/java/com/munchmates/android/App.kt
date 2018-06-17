package com.munchmates.android

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.ClubType
import com.munchmates.android.DatabaseObjs.CollegeType
import com.munchmates.android.DatabaseObjs.MPlanType
import com.munchmates.android.DatabaseObjs.MateType

class App {

    companion object {
        var current = 0

        var clubs = arrayListOf<String>()
        var colleges = arrayListOf<String>()
        var mates = arrayListOf<String>()
        var plans = arrayListOf<String>()

        fun init() {
            FirebaseDatabase.getInstance().reference.child("LISTS/clubsOrgs").addValueEventListener(TypeListener())
            while(current == 0);
            FirebaseDatabase.getInstance().reference.child("LISTS/colleges").addValueEventListener(TypeListener())
            while(current == 1);
            FirebaseDatabase.getInstance().reference.child("LISTS/mateTypes").addValueEventListener(TypeListener())
            while(current == 2);
            FirebaseDatabase.getInstance().reference.child("LISTS/mealPlan").addValueEventListener(TypeListener())
            while(current == 3);
        }
    }

    private class TypeListener: ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            when(current) {
                0 -> for(type in snapshot.children) clubs.add(type.getValue<ClubType>(ClubType::class.java)!!.clubsOrgsName)
                1 -> for(type in snapshot.children) colleges.add(type.getValue<CollegeType>(CollegeType::class.java)!!.collegeName)
                2 -> for(type in snapshot.children) mates.add(type.getValue<MateType>(MateType::class.java)!!.mateTypeName)
                3 -> for(type in snapshot.children) plans.add(type.getValue<MPlanType>(MPlanType::class.java)!!.mealPlanName)
            }
            current++
        }

        override fun onCancelled(error: DatabaseError) {}
    }
}