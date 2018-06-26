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
import com.google.firebase.database.FirebaseDatabase
import com.munchmates.android.DatabaseObjs.Sender
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.backgroundResource

class MessageActivity : AppCompatActivity() {

    val dialog = LoadingDialog(::respond)
    val usersRef = FirebaseDatabase.getInstance().reference
    val senders = arrayListOf<Sender>()
    val users = arrayListOf<User>()

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
                val adapter = SenderAdapter(this, senders, users)
                list_list_list.adapter = adapter
                list_list_list.onItemClickListener = adapter
                dialog.dismiss()
            }
        }
        else {
            users.add(snapshot.getValue<User>(User::class.java)!!)
            if(users.size == senders.size) {
                val adapter = SenderAdapter(this, senders, users)
                list_list_list.adapter = adapter
                list_list_list.onItemClickListener = adapter
                dialog.dismiss()
            }
        }
    }

    private class SenderAdapter(private val context: Context, private val senders: ArrayList<Sender>, private val users: ArrayList<User>): BaseAdapter(), AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().reference.child("USERS/$uid/conversations/senderList/$uid/read").setValue(true)

            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("uid", senders[pos].uid)
            context.startActivity(intent)
        }

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return senders.size
        }

        override fun getItem(position: Int): Any {
            return senders[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.item_search_result, parent, false)

            val sender = senders[pos]
            val user = users[pos]
            rowView.findViewById<TextView>(R.id.result_text_name).setText(sender.userDisplayName)
            rowView.findViewById<TextView>(R.id.result_text_class).setText(user.mateType)
            rowView.findViewById<TextView>(R.id.result_text_college).setText(user.college)

            if(user.mealPlan) {
                rowView.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            }

            if(!sender.read) {
                rowView.backgroundResource = R.color.colorAccent
            }

            return rowView
        }
    }
}