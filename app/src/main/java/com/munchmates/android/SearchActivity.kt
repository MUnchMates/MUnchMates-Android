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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchmates.android.DatabaseObjs.*
import kotlinx.android.synthetic.main.activity_list.*

class SearchActivity : AppCompatActivity(), ValueEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val type = intent.getIntExtra("type", 0)
        val group = intent.getStringExtra("group")
        println("Searching for a ${resources.getStringArray(R.array.groups)[type]} called $group")

        getResults(type, group)
    }

    private fun getResults(type: Int, group: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("USERS")
        when(type) {
            0 -> { // club
                usersRef.addValueEventListener(this)
            }
            1 -> { // college
                usersRef.orderByChild("college").equalTo(group).addValueEventListener(this)
            }
            2 -> { // mate type
                usersRef.orderByChild("mateType").equalTo(group).addValueEventListener(this)
            }
            3 -> { //meal plan
                usersRef.orderByChild("mealPlan").equalTo(group == "Yes").addValueEventListener(this)
            }
        }
    }

    override fun onCancelled(error: DatabaseError) {}

    override fun onDataChange(snapshot: DataSnapshot) {
        val users = arrayListOf<User>()
        for(user in snapshot.children) {
            val user = user.getValue<User>(User::class.java)!!
            if(intent.getIntExtra("type", 0) == 0) {
                for(club in user.clubsOrgs.values) {
                    if(club.clubsOrgsName == intent.getStringExtra("group")) users.add(user)
                }
            }
            else {
                users.add(user)
            }
        }

        users.shuffle()
        val adapter = UserAdapter(this, users)
        list_list_list.adapter = adapter
        list_list_list.onItemClickListener = adapter
    }

    private class UserAdapter(private val context: Context, private val list: ArrayList<User>): BaseAdapter(), AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val intent = Intent(context, ProfileActivity::class.java)
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
            val rowView = inflater.inflate(R.layout.item_search_result, parent, false)

            val user = list[pos]
            rowView.findViewById<TextView>(R.id.result_text_name).setText("${user.firstName} ${user.lastName}")
            rowView.findViewById<TextView>(R.id.result_text_college).setText("${user.college}")
            rowView.findViewById<TextView>(R.id.result_text_class).setText("${user.mateType}")

            if(user.mealPlan) {
                rowView.findViewById<TextView>(R.id.result_text_m).visibility = View.VISIBLE
            }

            return rowView
        }
    }
}