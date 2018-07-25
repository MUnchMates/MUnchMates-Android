package com.munchmates.android.Firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        if(msg.data.isNotEmpty()) {
            // check for data payload
        }

        if(msg.notification != null) {
            // check for notification payload
            println("New message from: " + msg.notification!!.title)
        }
    }
}