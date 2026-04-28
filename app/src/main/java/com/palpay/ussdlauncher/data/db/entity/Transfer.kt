package com.palpay.ussdlauncher.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientName: String,
    val phone: String,
    val amount: String,
    val serviceKey: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)
