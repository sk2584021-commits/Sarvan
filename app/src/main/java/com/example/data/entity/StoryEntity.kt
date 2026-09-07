package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val mediaUrl: String,
    val isVideo: Boolean,
    val createdAt: Long,
    val expiresAt: Long
)
