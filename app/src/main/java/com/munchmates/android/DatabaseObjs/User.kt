package com.munchmates.android.DatabaseObjs

data class User (
        val college: String = "",
        val emailNotifications: Boolean = false,
        val lastName: String = "",
        val muteMode: Boolean = false,
        val city: String = "",
        val stateCountry: String = "",
        val firstName: String = "",
        val uid: String = "",
        val mateType: String = "",
        val email: String = "",
        val mealPlan: Boolean = false,
        val clubsOrgs: HashMap<String, Club> = hashMapOf())