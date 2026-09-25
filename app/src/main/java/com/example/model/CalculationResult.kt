package com.example.model

data class CalculationResult(
    val quantity: Int = 1,
    val gramsPerItem: Double = 0.0,
    val totalGrams: Double = 0.0,
    val filamentCostPerKg: Double = 0.0,
    val materialCost: Double = 0.0,
    
    val printHours: Double = 0.0,
    val printerWatts: Double = 150.0,
    val energyCostPerKwh: Double = 0.85,
    val energyKwh: Double = 0.0,
    val energyCost: Double = 0.0,
    
    val extraCosts: Double = 0.0,
    val failureRatePercent: Double = 5.0,
    val failureCost: Double = 0.0,
    val depreciationPerHour: Double = 1.0,
    val depreciationCost: Double = 0.0,
    
    val totalProductionCost: Double = 0.0,
    val profitPercentage: Double = 100.0,
    val totalProfit: Double = 0.0,
    val totalFinalPrice: Double = 0.0,
    val unitPrice: Double = 0.0,
    
    val cashDiscountPercent: Double = 5.0,
    val cashPrice: Double = 0.0,
    val installments: Int = 2,
    val installmentValue: Double = 0.0
)
