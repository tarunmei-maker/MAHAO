package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageType: String, // "singju", "eromba", "kangshoi", "thali", "kheer", "generic"
    val isAvailable: Boolean = true,
    val prepTimeMinutes: Int = 15
) : Serializable
