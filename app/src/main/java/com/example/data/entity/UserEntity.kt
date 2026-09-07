package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val profilePicUrl: String?,
    val bio: String?,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isMonetizationEligible: Boolean = false,
    val isAdmin: Boolean = false
)
