package com.example.if570_lab_uts_hansphilemonlimanza_70710

data class Post(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val likes: Int = 0, // Number of likes
)