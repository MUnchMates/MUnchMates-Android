package com.munchmates.android

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_list.*

class ConversationActivity : AppCompatActivity(), ValueEventListener {

    var users: ArrayList<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        var uid = intent.getStringExtra("uid")
        FirebaseDatabase.getInstance().reference.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}").addValueEventListener(this)
        FirebaseDatabase.getInstance().reference.child("USERS/$uid").addValueEventListener(this)
        getMessages(uid)
    }

    private fun getMessages(uid: String) {
        var msgRef = FirebaseDatabase.getInstance().reference.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}/conversations/messageList/$uid/messages")
        msgRef.orderByChild("timeStamp").addValueEventListener(this)
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onDataChange(snapshot: DataSnapshot) {
        if(snapshot.ref.toString().contains("messages")) {
            val messages = arrayListOf<Message>()
            for(child in snapshot.children) {
                messages.add(child.getValue<Message>(Message::class.java)!!)
            }

            val adapter = MessagesAdapter(this, messages, users)
            list_list_list.adapter = adapter
        }
        else {
            users.add(snapshot.getValue<User>(User::class.java)!!)
        }
    }

    private class MessagesAdapter(private val context: Context, private val list: ArrayList<Message>, private val users: ArrayList<User>): BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val message = list[pos]
            rowView.findViewById<TextView>(android.R.id.text1).setText("${message.text}")
            rowView.findViewById<TextView>(android.R.id.text2).setText("From: ${message.sender_id}")
            for(user in users) {
                if(message.sender_id == user.uid) {
                    rowView.findViewById<TextView>(android.R.id.text2).setText("From: ${user.firstName} ${user.lastName}")
                }
            }

            return rowView
        }
    }
}