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
import com.munchmates.android.DatabaseObjs.Sender
import kotlinx.android.synthetic.main.activity_list.*

class MessageActivity : AppCompatActivity(), ValueEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        getMessages()
    }

    private fun getMessages() {
        val usersRef = FirebaseDatabase.getInstance().reference
        usersRef.child("USERS/${FirebaseAuth.getInstance().currentUser?.uid}/conversations/senderList").orderByChild("timeStamp").addValueEventListener(this)
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onDataChange(snapshot: DataSnapshot) {
        val senders = arrayListOf<Sender>()
        for(child in snapshot.children) {
            senders.add(child.getValue<Sender>(Sender::class.java)!!)
        }

        val adapter = SenderAdapter(this, senders)
        list_list_list.adapter = adapter
        list_list_list.onItemClickListener = adapter
    }

    private class SenderAdapter(private val context: Context, private val list: ArrayList<Sender>): BaseAdapter(), AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("uid", list[pos].uid)
            context.startActivity(intent)
        }

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
            rowView.findViewById<TextView>(android.R.id.text1).setText("${sender.userDisplayName}")
            var read = "Not read"
            if(sender.read) read = "Read"
            rowView.findViewById<TextView>(android.R.id.text2).setText("Last Message: $read")
            //rowView.findViewById<TextView>(R.id.result_text_college).setText("${user.college}")
            //rowView.findViewById<TextView>(R.id.result_text_class).setText("${user.mateType}")

            //if(user.mealPlan) {
            //    rowView.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            //}

            return rowView
        }
    }
}