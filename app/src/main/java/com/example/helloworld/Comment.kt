package com.example.helloworld

data class Comment(
    val id: String = "",
    val postId: String = "",
    val content: String = "",
    val author: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
