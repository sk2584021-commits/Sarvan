package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val content: String?,
    val type: String, // "TEXT", "IMAGE", "VIDEO"
    val mediaUrl: String?,
    val createdAt: Long,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isReported: Boolean = false
)
