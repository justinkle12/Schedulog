package com.example.schedulog

/* Model Object for a single event post */
data class PostItem(
    val title : String = "",
    val description: String = "",
    val imageURL: String = "",
    val user: String = "",
    val date: Long = 0,
    val startEndTime: String = "",
    val tags: MutableList<String> = mutableListOf(),
    val eventKey: String = "",
    val timestamp: Long = 0,
    val average_rating: Float = 0.0f,
)
