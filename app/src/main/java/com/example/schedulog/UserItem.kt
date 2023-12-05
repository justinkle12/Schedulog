package com.example.schedulog

/* Model Object for a single event post */
data class UserItem(
    val eventName: String = "",
    val profileURL: String = "",
    val user: String = "",
    val date: Long = 0,
    val eventKey : String = "",
    val title : String = "",
    val attendingUsers: Map<String, Boolean> = emptyMap(),


)
