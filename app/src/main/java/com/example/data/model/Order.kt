package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val itemsSummary: String, // E.g., "Standard Singju x2, Chak-hao Kheer x1"
    val totalAmount: Double,
    val status: String, // "Pending", "Preparing", "Out for Delivery", "Delivered"
    val orderTimeEpoch: Long = System.currentTimeMillis(),
    val progressPercent: Float = 0.0f,
    // Driving coordinates for tracking simulation (Imphal coordinates as default)
    val driverLatitude: Double = 24.8170, // Default Imphal center
    val driverLongitude: Double = 93.9368,
    val driverName: String = "Chaoba Singh",
    val driverPhone: String = "+91 98765 43210",
    val etaMinutes: Int = 25,
    val paymentMethod: String = "Cash on Delivery",
    val discountAmount: Double = 0.0
) : Serializable
