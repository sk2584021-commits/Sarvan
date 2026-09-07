package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monetization_applications")
data class MonetizationAppEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val followersAtApplication: Int,
    val watchTimeAtApplication: Int,
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val appliedAt: Long
)
