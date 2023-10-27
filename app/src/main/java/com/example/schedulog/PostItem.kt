package com.example.schedulog

/* Model Object for a single event post */
data class PostItem(
    val post_id : Int = 0,
    val description: String = "",
    val image_url: String = "",
    val timestamp: Long = 0,
    val average_rating: Float = 0f,
    val user_id: String = ""
)
