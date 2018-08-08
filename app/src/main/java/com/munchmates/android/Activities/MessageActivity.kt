package com.munchmates.android.Activities

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
import com.munchmates.android.App
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import com.munchmates.android.Firebase.LoadingDialog
import com.munchmates.android.R
import com.munchmates.android.Utils
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.backgroundResource

class MessageActivity : BaseMMActivity(), View.OnClickListener {
    var results = hashMapOf<String, View>()
    var senders = arrayListOf<Sender>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        title = "Messages"
    }

    override fun onResume() {
        super.onResume()
        getMessages()
    }

    private fun getMessages() {
        senders = arrayListOf()
        val sorted = Utils.sortSender(App.user.conversations.senderList)
        for(sender in sorted) {
            senders.add(sender)
        }
        fill()
    }

    private fun fill() {
        results = hashMapOf()
        list_list_list.removeAllViews()
        for(sender in senders) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_search_result, list_list_list as ViewGroup, false)

            val user = App.users[sender.uid]!!
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