package com.munchmates.android.Activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.MsgObj
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.Firebase.LoadingDialog
import com.munchmates.android.R
import kotlinx.android.synthetic.main.activity_conversation.*
import java.text.SimpleDateFormat
import java.util.*

class ConversationActivity : AppCompatActivity(), View.OnClickListener {

    val dialog = LoadingDialog(::respond)
    val usersRef = FirebaseDatabase.getInstance().reference
    var them = User()
    var messages = arrayListOf<Message>()
    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        conv_button_send.setOnClickListener(this)

        uid = intent.getStringExtra("uid")
        getMessages()
    }

    private fun getMessages() {
        dialog.show(fragmentManager.beginTransaction(), "dialog")
        listMessages()

        usersRef.child("USERS/$uid").addValueEventListener(dialog)
    }

    private fun listMessages() {
        messages = arrayListOf()
        if (App.user.conversations.messageList.contains(uid)) {
            for (message in App.user.conversations.messageList[uid]!!.messages.values) {
                messages.add(message)
            }
        }
    }

    private fun buildList() {
        conv_list_msgs.removeAllViews()
        for (msg in messages) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_message, conv_list_msgs as ViewGroup, false)

            view.findViewById<TextView>(R.id.msg_text_msg).text = msg.text
            view.findViewById<TextView>(R.id.msg_text_sender).text = "UID: ${msg.sender_id}"
            view.findViewById<TextView>(R.id.msg_text_date).text = msg.dateTime

            var user = App.user
            if (msg.sender_id == them.uid) {
                user = them
            }
            view.findViewById<TextView>(R.id.msg_text_sender).text = "${user.firstName} ${user.lastName}"

            if (msg.sender_id == App.user.uid) {
                view.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }
            conv_list_msgs.addView(view)
            conv_list_msgs.addView(LayoutInflater.from(this).inflate(R.layout.spacer, conv_list_msgs as ViewGroup, false))
        }
    }

    private fun respond(snapshot: DataSnapshot) {
        them = snapshot.getValue<User>(User::class.java)!!
        buildList()
        dialog.dismiss()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.conv_button_send -> {
                val message = conv_edit_msg.text.toString()
                conv_edit_msg.setText("")
                var newMsg =  Message("${App.user.firstName} ${App.user.lastName}", App.user.uid, message, SimpleDateFormat("M.d.yyyy • h:mma").format(Date()), System.currentTimeMillis() / 1000.0)
                addMessage(newMsg, App.user, them)
                addMessage(newMsg, them, App.user)
            }
        }
    }

    private fun addMessage(newMsg: Message, user: User, other: User) {
        val uid = other.uid
        val convos = user.conversations.messageList
        if(!convos.keys.contains(uid)) {
            convos[uid] = MsgObj()
        }
        val msgRef = usersRef.child("USERS/${user.uid}/conversations/messageList/$uid/messages/")
        msgRef.push().setValue(newMsg)

        var read = true
        if(user == them) {
            read = false
        }
        val contact = Sender(read, other.uid, "${other.firstName} ${other.lastName}", -(System.currentTimeMillis() / 1000.0))
        val sendRef = usersRef.child("USERS/${user.uid}/conversations/senderList/$uid/")
        sendRef.setValue(contact)
    }
}