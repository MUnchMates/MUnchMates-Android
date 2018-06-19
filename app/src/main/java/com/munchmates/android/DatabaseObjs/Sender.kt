package com.munchmates.android.DatabaseObjs

data class Sender (
    val read: Boolean = false,
    val uid: String = "",
    val userDisplayName: String = "",
    val timeStamp: Double = 0.0)