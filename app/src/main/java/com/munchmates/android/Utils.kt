package com.munchmates.android

import com.munchmates.android.DatabaseObjs.Message
import com.munchmates.android.DatabaseObjs.Sender
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Utils {
    companion object {
        val messageFormat = "M.d.yyyy • h:mma"
        val userFormat = "M.d.yyyy • H:mm:ss"

        fun sortSender(senders: HashMap<String, Sender>): ArrayList<Sender> {
            val unsorted = HashMap(senders)
            val sorted = arrayListOf<Sender>()
            while(unsorted.size > 0) {
                var lowest = ""
                var timestamp = 0.0
                for(sender in unsorted) {
                    if(sender.value.timeStamp < timestamp || lowest == "") {
                        timestamp = sender.value.timeStamp
                        lowest = sender.key
                    }
                }
                sorted.add(unsorted[lowest]!!)
                unsorted.remove(lowest)
            }
            return sorted
        }

        fun sortMessage(messages: HashMap<String, Message>): ArrayList<Message> {
            val sorted = arrayListOf<Message>()
            while(messages.size > 0) {
                var lowest = ""
                var timestamp = 0.0
                for(message in messages) {
                    if(message.value.timeStamp < timestamp || lowest == "") {
                        timestamp = message.value.timeStamp
                        lowest = message.key
                    }
                }
                sorted.add(messages[lowest]!!)
                messages.remove(lowest)
            }
            return sorted
        }

        fun getDate(format: String): String {
            return SimpleDateFormat(format).format(Date())
        }
    }
}