package com.munchmates.android.Firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {
    val MESSAGE_ID = "MATE_MESSAGE"

    override fun onMessageReceived(msg: RemoteMessage) {
        if(msg.data.isNotEmpty()) {
            // check for data payload
            println("New data from: " + msg.data["title"])
            //sendNotification(msg.data["title"]!!, msg.data["body"]!!, msg.data["sender"]!!, msg.data["date"]!!)
        }

        if(msg.notification != null) {
            // check for notification payload
            println("New notification from: " + msg.notification!!.title)
        }
    }

    // disabled bc only seems to only while running, wasn't a problem before
    /*
    private fun sendNotification(title: String, body: String, uid: String, date: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Mate Message"
            val description = "New messages from mates."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MESSAGE_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }

        val mBuilder = NotificationCompat.Builder(this, MESSAGE_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, mBuilder.build())
    }*/
}