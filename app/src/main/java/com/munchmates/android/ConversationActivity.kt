package com.munchmates.android

import android.content.Context
import android.graphics.Color
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

class ConversationActivity : AppCompatActivity() {

    val dialog = LoadingDialog(::respond)
    val usersRef = FirebaseDatabase.getInstance().reference
    val users = arrayListOf<User>()
    val messages = arrayListOf<Message>()
    var done = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        var uid = intent.getStringExtra("uid")
        getMessages(uid)
    }

    private fun getMessages(uid: String) {
        dialog.show(fragmentManager.beginTransaction(), "dialog")

        usersRef.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}/conversations/messageList/$uid/messages").orderByChild("timeStamp").addValueEventListener(dialog)
        usersRef.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}").addValueEventListener(dialog)
        usersRef.child("USERS/$uid").addValueEventListener(dialog)
    }

    private fun respond(snapshot: DataSnapshot) {
        if(snapshot.ref.toString().contains("messages")) {
            for(child in snapshot.children) {
                messages.add(child.getValue<Message>(Message::class.java)!!)
            }
            done = true
        }
        else {
            users.add(snapshot.getValue<User>(User::class.java)!!)
        }

        if(done && users.size == 2) {
            val adapter = MessagesAdapter(this, messages, users, intent.getStringExtra("uid"))
            list_list_list.adapter = adapter
            dialog.dismiss()
        }
    }

    private class MessagesAdapter(private val context: Context, private val list: ArrayList<Message>, private val users: ArrayList<User>, private val me: String): BaseAdapter() {

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
            val rowView = inflater.inflate(R.layout.item_message, parent, false)

            val message = list[pos]
            rowView.findViewById<TextView>(R.id.msg_text_msg).setText(message.text)
            rowView.findViewById<TextView>(R.id.msg_text_sender).setText("UID: ${message.sender_id}")
            rowView.findViewById<TextView>(R.id.msg_text_date).setText(message.dateTime)
            for(user in users) {
                if(message.sender_id == user.uid) {
                    rowView.findViewById<TextView>(R.id.msg_text_sender).setText("${user.firstName} ${user.lastName}")
                }
            }

            if(message.sender_id == me) {
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }

            return rowView
        }
    }
}