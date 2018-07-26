package com.munchmates.android

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
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
            FirebaseDatabase.getInstance().reference.child("LISTS/clubsOrgs").addListenerForSingleValueEvent(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/colleges").addListenerForSingleValueEvent(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/mateTypes").addListenerForSingleValueEvent(dialog)
            FirebaseDatabase.getInstance().reference.child("LISTS/mealPlan").addListenerForSingleValueEvent(dialog)
            FirebaseDatabase.getInstance().reference.child("USERS/$uid").addValueEventListener(dialog)
            FirebaseDatabase.getInstance().reference.child("USERS/").addListenerForSingleValueEvent(dialog)
            while(current < 6);
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            println("getInstanceId failed $task.exception")
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result.token
                        println("New token: $token")

                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        FirebaseDatabase.getInstance().reference.child("USERS/$uid/instanceId").setValue(token)
                    })
            dialog.dismiss()
        }

        private fun respond(snapshot: DataSnapshot) {
            println("Fetching data")
            val refStr = snapshot.ref.toString()
            println("Loading $refStr")
            if(refStr.contains("clubsOrgs")) {
                clubs = arrayListOf()
                for(type in snapshot.children) clubs.add(type.getValue<Club>(Club::class.java)!!)
            }
            else if(refStr.contains("colleges")) {
                colleges = arrayListOf()
                for(type in snapshot.children) colleges.add(type.getValue<CollegeType>(CollegeType::class.java)!!)
            }
            else if(refStr.contains("mateTypes")) {
                mates = arrayListOf()
                for(type in snapshot.children) mates.add(type.getValue<MateType>(MateType::class.java)!!)
            }
            else if(refStr.contains("mealPlan")) {
                plans = arrayListOf()
                for(type in snapshot.children) plans.add(type.getValue<MPlanType>(MPlanType::class.java)!!)
            }
            else if(refStr.endsWith("USERS")) {
                users = hashMapOf()
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
            else if(refStr.contains("USERS")) {
                user = snapshot.getValue<User>(User::class.java)!!
            }
            current++
        }
    }
}