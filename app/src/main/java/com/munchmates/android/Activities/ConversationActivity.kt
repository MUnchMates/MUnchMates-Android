package com.munchmates.android.Activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.MsgObj
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.R
import com.munchmates.android.Utils
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : BaseMMActivity(), View.OnClickListener {

    val usersRef = FirebaseDatabase.getInstance().reference
    var messages = arrayListOf<Message>()
    var uid = ""
    var them = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        conv_button_send.setOnClickListener(this)

        uid = intent.getStringExtra("uid")
        them = App.users[uid]!!
        listMessages()
    }

    private fun listMessages() {
        messages = arrayListOf()
        if (App.user.conversations.messageList.contains(uid)) {
            // sort and remove old messages
            messages = Utils.sortMessage(App.user.conversations.messageList[uid]!!.messages, uid)

            // remove messages from db
            val usersRef = FirebaseDatabase.getInstance().reference.child("USERS/${App.user.uid}/conversations/messageList/$uid/messages")
            usersRef.setValue(App.user.conversations.messageList[uid]!!.messages)
        }

        buildList()
    }

    private fun buildList() {
        conv_list_msgs.removeAllViews()
        for (msg in messages) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_message, conv_list_msgs as ViewGroup, false)

            view.findViewById<TextView>(R.id.msg_text_msg).text = msg.text
            view.findViewById<TextView>(R.id.msg_text_sender).text = "UID: ${msg.sender_id}"
            view.findViewById<TextView>(R.id.msg_text_date).text = msg.dateTime

            var user = App.user
            if (msg.sender_id == uid) {
                user = them
            }
            view.findViewById<TextView>(R.id.msg_text_sender).text = "${user.firstName} ${user.lastName}"

            if (msg.sender_id == App.user.uid) {
                view.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }
            conv_list_msgs.addView(view)
            conv_list_msgs.addView(LayoutInflater.from(this).inflate(R.layout.spacer, conv_list_msgs as ViewGroup, false))
        }
        conv_scroll.post { conv_scroll.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.conv_button_send -> {
                val message = conv_edit_msg.text.toString()
                conv_edit_msg.setText("")
                var newMsg =  Message("${App.user.firstName} ${App.user.lastName}", App.user.uid, message, Utils.getDate(Utils.messageFormat), System.currentTimeMillis() / 1000.0)
                addMessage(newMsg, App.user, them)
                addMessage(newMsg, them, App.user)
                messages.add(newMsg)
                buildList()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.conversation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}