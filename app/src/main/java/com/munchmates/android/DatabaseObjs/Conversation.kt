package com.munchmates.android.DatabaseObjs

data class Conversation (
        var messageList: HashMap<String, MsgObj> = hashMapOf(),
        var senderList: HashMap<String, Sender> = hashMapOf())