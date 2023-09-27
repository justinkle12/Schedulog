package com.example.schedulog

/* Model Object for a single event post */
data class PostItem(
    val description: String = "",
    val image_url: String = "",
    val timestamp: Long = 0,
    val user_id: String = ""
)
