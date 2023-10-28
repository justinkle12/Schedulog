package com.example.schedulog

/* Model Object for a single event post */
data class PostItem(
    val title : String = "",
    val description: String = "",
    val image_url: String = "",
    val timestamp: Long = 0,
    val user: String = "",
    val date: String = "", // TODO change this to long for milliseconds
    val startEndTime: String = "",
    val tags: MutableList<String> = mutableListOf(),
)
