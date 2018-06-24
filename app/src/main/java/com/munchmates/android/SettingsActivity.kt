package com.munchmates.android

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.munchmates.android.DatabaseObjs.User
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    var usersRef = FirebaseDatabase.getInstance().reference
    var stoRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://munch-mates-marquette.appspot.com/imgProfilePictures/")
    var user = User()
    val CODE = 7
    var newImage: Bitmap? = null
    val dialog = LoadingDialog(::respond)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings_button_logout.setOnClickListener(this)
        settings_button_save.setOnClickListener(this)
        settings_text_head.setOnClickListener(this)

        val gAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, App.mates)
        gAdapter.setDropDownViewResource(R.layout.item_spinner)
        settings_spinner_type.adapter = gAdapter

        val cAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, App.colleges)
        cAdapter.setDropDownViewResource(R.layout.item_spinner)
        settings_spinner_school.adapter = cAdapter

        fillPage()
    }

    private fun fillPage() {
        dialog.show(fragmentManager.beginTransaction(), "dialog")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        usersRef = usersRef.child("USERS/$uid")
        usersRef.addValueEventListener(dialog)

        stoRef = stoRef.child("$uid.png")
        Glide.with(this)
                .load(stoRef)
                .into(settings_image_head)
    }

    fun respond(snapshot: DataSnapshot) {
        user = snapshot.getValue<User>(User::class.java)!!

        settings_edit_first.setText(user.firstName)
        settings_edit_last.setText(user.lastName)
        settings_edit_town.setText(user.city)
        settings_edit_state.setText(user.stateCountry)

        settings_switch_mute.isChecked = user.muteMode
        settings_switch_meal.isChecked = user.mealPlan
        settings_switch_notif.isChecked = user.emailNotifications

        dialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CODE && resultCode == Activity.RESULT_OK && data != null) {
            val inStream = contentResolver.openInputStream(data.data)
            newImage = BitmapFactory.decodeStream(inStream)
            val width = newImage!!.width
            val height = newImage!!.height
            val widthSmaller = width < height
            if(widthSmaller) {
                val delta = height - width
                newImage = Bitmap.createBitmap(newImage, 0, delta/2, width, width)
            }
            else {
                val delta = width - height
                newImage = Bitmap.createBitmap(newImage, delta/2, 0, height, height)
            }
            newImage = Bitmap.createScaledBitmap(newImage, 1024, 1024, false)
            settings_image_head.setImageBitmap(newImage)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings_button_logout -> {
                Prefs.instance.put(Prefs.EMAIL_PREF, "")
                Prefs.instance.put(Prefs.PASSWORD_PREF, "")
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MMActivity::class.java))
            }
            R.id.settings_text_head -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), CODE)
            }
            R.id.settings_button_save -> {
                user.firstName = settings_edit_first.text.toString()
                user.lastName = settings_edit_last.text.toString()
                user.city = settings_edit_town.text.toString()
                user.stateCountry = settings_edit_state.text.toString()
                user.mateType = settings_spinner_type.selectedItem as String
                user.college = settings_spinner_school.selectedItem as String
                user.muteMode = settings_switch_mute.isChecked
                user.mealPlan = settings_switch_meal.isChecked
                user.emailNotifications = settings_switch_notif.isChecked
                usersRef.setValue(user)

                if(newImage != null) {
                    val stream = ByteArrayOutputStream()
                    newImage!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stoRef.putBytes(stream.toByteArray())
                }
                finish()
            }
        }
    }
}