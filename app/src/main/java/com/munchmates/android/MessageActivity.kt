package com.munchmates.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.backgroundResource

class MessageActivity : AppCompatActivity(), View.OnClickListener {

    val dialog = LoadingDialog(::respond)
    val usersRef = FirebaseDatabase.getInstance().reference
    val senders = arrayListOf<Sender>()
    val users = arrayListOf<User>()
    var results = hashMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        getMessages()
    }

    private fun getMessages() {
        dialog.show(fragmentManager.beginTransaction(), "dialog")

        usersRef.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}/conversations/senderList").orderByChild("timeStamp").addValueEventListener(dialog)
    }

    private fun respond(snapshot: DataSnapshot) {
        if(snapshot.ref.toString().contains("senderList")) {
            for(child in snapshot.children) {
                val sender = child.getValue<Sender>(Sender::class.java)!!
                print("Requesting ${sender.uid}")
                usersRef.child("USERS/${sender.uid}").addValueEventListener(dialog)
                senders.add(sender)
            }
            if(snapshot.childrenCount == 0L) {
                fill()
            }
        }
        else {
            users.add(snapshot.getValue<User>(User::class.java)!!)
            if(users.size == senders.size) {
                fill()
            }
        }
    }

    private fun fill() {
        results = hashMapOf()
        list_list_list.removeAllViews()
        for(i in 0 until senders.size) {
            println("Adding view")
            val sender = senders[i]
            val view = LayoutInflater.from(this).inflate(R.layout.item_search_result, list_list_list as ViewGroup, false)

            val user = users[i]
            view.findViewById<TextView>(R.id.result_text_name).text = sender.userDisplayName
            view.findViewById<TextView>(R.id.result_text_class).text = user.mateType
            view.findViewById<TextView>(R.id.result_text_college).text = user.college

            if(user.mealPlan) {
                view.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            }

            if(!sender.read) {
                view.backgroundResource = R.color.colorAccent
            }
            view.setOnClickListener(this)
            list_list_list.addView(view)
            list_list_list.addView(LayoutInflater.from(this).inflate(R.layout.spacer, list_list_list as ViewGroup, false))
            results[sender.uid] = view
        }
        dialog.dismiss()
    }

    override fun onClick(v: View?) {
        for(senderId in results.keys) {
            if(v == results.getValue(senderId)) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().reference.child("USERS/$uid/conversations/senderList/$senderId/read").setValue(true)

                val intent = Intent(this, ConversationActivity::class.java)
                intent.putExtra("uid", senderId)
                startActivity(intent)
            }
        }
    }
}