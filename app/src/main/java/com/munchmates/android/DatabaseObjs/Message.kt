package com.munchmates.android.DatabaseObjs

data class Message (
    val name: String = "",
    val sender_id: String = "",
    val text: String = "",
    var dateTime: String = "",
    var timeStamp: Double = 0.0)