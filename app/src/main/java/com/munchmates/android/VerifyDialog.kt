package com.munchmates.android

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.munchmates.android.Activities.HomeActivity


@SuppressLint("ValidFragment")
class VerifyDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return AlertDialog.Builder(activity)
                .setTitle("Verify Your Account")
                .setMessage("Please verify your account and relaunch the app.")
                .setPositiveButton("Recheck") { dialog, whichButton -> FirebaseAuth.getInstance().currentUser!!.reload() }
                .setNegativeButton("Resend") { dialog, whichButton -> FirebaseAuth.getInstance().currentUser!!.sendEmailVerification() }.show()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        (activity as HomeActivity).checkVerification()
    }
}