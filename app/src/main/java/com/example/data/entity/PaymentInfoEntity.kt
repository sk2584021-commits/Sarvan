package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_info")
data class PaymentInfoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val accountHolder: String,
    val bankName: String,
    val accountNumber: String,
    val ifsc: String,
    val accountType: String,
    val country: String,
    val taxPanNumber: String?
)
