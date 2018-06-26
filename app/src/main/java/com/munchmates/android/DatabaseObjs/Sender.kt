package com.munchmates.android.DatabaseObjs

data class Sender (
    var read: Boolean = false,
    val uid: String = "",
    val userDisplayName: String = "",
    var timeStamp: Double = 0.0)