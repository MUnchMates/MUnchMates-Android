package com.munchmates.android.munchmates

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        home_button_search.setOnClickListener(this)
        home_button_messages.setOnClickListener(this)

        val tAdapter = ArrayAdapter(this, R.layout.item_link, resources.getStringArray(R.array.groups))
        tAdapter.setDropDownViewResource(R.layout.item_spinner)
        home_spinner_type.adapter = tAdapter
        home_spinner_type.onItemSelectedListener = this

        val gAdapter = ArrayAdapter(this, R.layout.item_link, arrayOf("all"))
        gAdapter.setDropDownViewResource(R.layout.item_spinner)
        home_spinner_group.adapter = gAdapter
        home_spinner_group.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        home_text_whatgroup.text = "Which ${resources.getStringArray(R.array.groups)[home_spinner_type.selectedItemPosition]}?"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.home_button_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.home_button_messages -> {
                startActivity(Intent(this, MessageActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}