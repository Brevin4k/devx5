package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val printerName: String = "Minha Impressora 3D",
    val printerWatts: Double = 150.0,
    val energyCostPerKwh: Double = 0.85,
    val defaultProfitMargin: Double = 100.0,
    val defaultDepreciationPerHour: Double = 1.0,
    val defaultFailureRatePercent: Double = 5.0,
    val defaultCashDiscountPercent: Double = 5.0,
    val defaultInstallments: Int = 2
)
