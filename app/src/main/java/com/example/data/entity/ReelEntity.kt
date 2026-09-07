package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val videoUrl: String,
    val caption: String?,
    val createdAt: Long,
    val likesCount: Int = 0,
    val commentsCount: Int = 0
)
