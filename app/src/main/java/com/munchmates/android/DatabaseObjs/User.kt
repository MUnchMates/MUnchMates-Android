package com.munchmates.android.DatabaseObjs

data class User (
        var uid: String = "",
        var email: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var city: String = "",
        var stateCountry: String = "",
        var mateType: String = "",
        var college: String = "",
        var emailNotifications: Boolean = false,
        var muteMode: Boolean = false,
        var mealPlan: Boolean = false,
        var searchOrderNumber: Int = 0,
        var lastOpened: String = "",
        var clubsOrgs: HashMap<String, Club> = hashMapOf(),
        var conversations: Conversation = Conversation())