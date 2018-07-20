package com.munchmates.android.Firebase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.MotionEvent
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
        print(error.message)
    }

    var bar: ProgressBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bar = ProgressBar(activity)

        isCancelable = false

        val builder = AlertDialog.Builder(activity)
        builder.setView(bar)
        return builder.create()
    }
}