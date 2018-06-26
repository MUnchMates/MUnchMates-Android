package com.munchmates.android

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.ProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@SuppressLint("ValidFragment")
class LoadingDialog(private val respond: (snapshot: DataSnapshot) -> Unit): DialogFragment(), ValueEventListener {

    override fun onDataChange(snapshot: DataSnapshot) {
        respond(snapshot)
    }

    override fun onCancelled(error: DatabaseError) {
        error("Cancelled!!!")
    }

    var bar: ProgressBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bar = ProgressBar(activity)

        val builder = AlertDialog.Builder(activity)
        builder.setView(bar)
        return builder.create()
    }
}