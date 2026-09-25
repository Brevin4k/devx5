package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // PLA, PETG, ABS, TPU, RESINA, NYLON, OUTRO
    val pricePerKg: Double,
    val brand: String = "",
    val color: String = "",
    val isDefault: Boolean = false
)
