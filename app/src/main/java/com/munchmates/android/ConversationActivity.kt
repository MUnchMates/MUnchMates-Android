package com.munchmates.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.Sender
import kotlinx.android.synthetic.main.activity_list.*

class ConversationActivity : AppCompatActivity(), ValueEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        getMessages(intent.getStringExtra("uid"))
    }

    private fun getMessages(uid: String) {
        val usersRef = FirebaseDatabase.getInstance().reference
        usersRef.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}/conversations/messageList/$uid/messages").orderByChild("timeStamp").addValueEventListener(this)
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onDataChange(snapshot: DataSnapshot) {
        val messages = arrayListOf<Message>()
        for(child in snapshot.children) {
            messages.add(child.getValue<Message>(Message::class.java)!!)
        }

        val adapter = MessagesAdapter(this, messages)
        list_list_list.adapter = adapter
    }

    private class MessagesAdapter(private val context: Context, private val list: ArrayList<Message>): BaseAdapter() {

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

            val sender = list[pos]
            rowView.findViewById<TextView>(android.R.id.text1).setText("${sender.text}")
            rowView.findViewById<TextView>(android.R.id.text2).setText("From: ${sender.sender_id}")
            //rowView.findViewById<TextView>(R.id.result_text_college).setText("${user.college}")
            //rowView.findViewById<TextView>(R.id.result_text_class).setText("${user.mateType}")

            //if(user.mealPlan) {
            //    rowView.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            //}

            return rowView
        }
    }
}