package com.munchmates.android.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.*
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.MsgObj
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.R
import com.munchmates.android.Utils
import kotlinx.android.synthetic.main.activity_conversation.*
import android.support.v7.widget.DividerItemDecoration

/**
 * This uses Recycler Views...
 * I haven't done these before so it may be jank...
 * Please fix if you know how to do better...
 * Particularly with adding new items
 */
class ConversationActivity : BaseMMActivity(), View.OnClickListener, Runnable {

    val usersRef = FirebaseDatabase.getInstance().reference
    var messages = arrayListOf<Message>()
    var uid = ""
    var them = User()
    var count = 0
    lateinit var cAdapter: RecyclerView.Adapter<*>
    lateinit var manager: LinearLayoutManager
    lateinit var thread: Thread
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        conv_button_send.setOnClickListener(this)

        uid = intent.getStringExtra("uid")
    }

    override fun onResume() {
        super.onResume()
        them = App.users[uid]!!
        title = "${them.firstName} ${them.lastName}"

        if(App.users[uid]!!.muteMode) {
            conv_text_mute.visibility = View.VISIBLE
            conv_layout_new.visibility = View.GONE
        }
        else {
            conv_text_mute.visibility = View.GONE
            conv_layout_new.visibility = View.VISIBLE
        }

        manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        thread = Thread(this)
        running = true
        thread.start()
    }

    override fun onPause() {
        super.onPause()
        running = false
    }

    override fun run() {
        while(running) {
            if(App.user.conversations.messageList[uid] != null) {
                val total = App.user.conversations.messageList[uid]!!.messages.size
                if(total != count) {
                    count = total
                    runOnUiThread {
                        listMessages()
                    }
                }
                Thread.sleep(2500)
            }
        }
    }

    private fun listMessages() {
        messages.removeAll(messages)
        if (App.user.conversations.messageList.contains(uid)) {
            // sort and remove old messages
            messages.addAll(Utils.sortMessage(App.user.conversations.messageList[uid]!!.messages, uid))

            // remove messages from db
            val usersRef = FirebaseDatabase.getInstance().reference.child("USERS/${App.user.uid}/conversations/messageList/$uid/messages")
            usersRef.setValue(App.user.conversations.messageList[uid]!!.messages)
        }

        buildList()
    }

    private fun buildList() {
        cAdapter = ConvAdapter(messages.toTypedArray())
        val recyclerView = findViewById<RecyclerView>(R.id.conv_list_msgs).apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = cAdapter
        }
        for(i in 0 until recyclerView.itemDecorationCount) {
            recyclerView.removeItemDecorationAt(i)
        }
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, manager.orientation))
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.conv_button_send -> {
                val message = conv_edit_msg.text.toString()
                conv_edit_msg.setText("")
                var newMsg =  Message("${App.user.firstName} ${App.user.lastName}", App.user.uid, message, Utils.getDate(Utils.messageFormat), System.currentTimeMillis() / 1000.0)
                if(!App.users[uid]!!.muteMode && message.isNotEmpty()) {
                    addMessage(newMsg, App.user, them)
                    addMessage(newMsg, them, App.user)
                }
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

    class ConvAdapter(private val data: Array<Message>): RecyclerView.Adapter<ConvAdapter.ViewHolder>() {
        class ViewHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false) as LinearLayout
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.layout
            val msg = data[position]
            view.findViewById<TextView>(R.id.msg_text_msg).text = msg.text
            view.findViewById<TextView>(R.id.msg_text_sender).text = "UID: ${msg.sender_id}"
            view.findViewById<TextView>(R.id.msg_text_date).text = msg.dateTime

            var user = App.users[msg.sender_id]!!
            view.findViewById<TextView>(R.id.msg_text_sender).text = "${user.firstName} ${user.lastName}"

            if (msg.sender_id == App.user.uid) {
                view.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }
        }

        override fun getItemCount(): Int = data.size
    }
}