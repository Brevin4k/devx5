package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val projectName: String,
    val quantity: Int,
    val materialName: String,
    val filamentGrams: Double,
    val filamentCostPerKg: Double,
    val printHours: Double,
    val printerWatts: Double,
    val energyCostPerKwh: Double,
    val extraCosts: Double,
    val extraCostsDescription: String,
    val profitPercentage: Double,
    val totalProductionCost: Double,
    val totalProfit: Double,
    val totalFinalPrice: Double,
    val paymentMethod: String,
    val installments: Int,
    val installmentValue: Double,
    val status: String = "ORCAMENTO", // ORCAMENTO, EM_IMPRESSAO, CONCLUIDO, ENTREGUE
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
