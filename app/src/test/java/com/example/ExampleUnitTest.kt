package com.example

import com.example.model.CalculationResult
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPrintCalculationLogic() {
        val qty = 10
        val gramsPerItem = 35.0
        val totalGrams = gramsPerItem * qty // 350g
        val filamentCostPerKg = 100.0 // R$ 100 / kg -> R$ 35,00
        val materialCost = (totalGrams / 1000.0) * filamentCostPerKg
        assertEquals(35.0, materialCost, 0.001)

        val totalHours = 10.0 // 10h
        val watts = 150.0 // 150W = 0.15 kW
        val kwhPrice = 0.80 // R$ 0.80/kWh
        val energyKwh = (watts / 1000.0) * totalHours // 1.5 kWh
        val energyCost = energyKwh * kwhPrice // R$ 1.20
        assertEquals(1.20, energyCost, 0.001)

        val extraCosts = 10.0 // Screws, inserts
        val depreciation = 1.0 * totalHours // R$ 10.00
        val totalProductionCost = materialCost + energyCost + extraCosts + depreciation // 35 + 1.2 + 10 + 10 = 56.20
        assertEquals(56.20, totalProductionCost, 0.001)

        val profitPercent = 100.0 // 100% margin
        val profit = totalProductionCost * (profitPercent / 100.0) // 56.20
        val totalPrice = totalProductionCost + profit // 112.40
        assertEquals(112.40, totalPrice, 0.001)

        val installments = 2
        val installmentValue = totalPrice / installments // 56.20
        assertEquals(56.20, installmentValue, 0.001)
    }
}
