package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.MaterialEntity
import com.example.data.OrderEntity
import com.example.data.SettingsEntity
import com.example.model.CalculationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(database.appDao())
    }

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaterials: StateFlow<List<MaterialEntity>> = repository.allMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<SettingsEntity> = repository.settings
        .combine(MutableStateFlow(SettingsEntity())) { saved, default ->
            saved ?: default
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsEntity())

    // --- Calculator Inputs ---
    var clientName = MutableStateFlow("")
        private set
    var projectName = MutableStateFlow("")
        private set
    var quantityText = MutableStateFlow("10")
        private set
    var gramsText = MutableStateFlow("35")
        private set
    var isGramsPerItem = MutableStateFlow(true)
        private set
    var printHoursText = MutableStateFlow("1")
        private set
    var printMinutesText = MutableStateFlow("15")
        private set
    var isTimePerItem = MutableStateFlow(true)
        private set
    var selectedMaterial = MutableStateFlow<MaterialEntity?>(null)
        private set
    var customMaterialPriceText = MutableStateFlow("100.00")
        private set
    var extraCostsText = MutableStateFlow("0.00")
        private set
    var extraCostsDescription = MutableStateFlow("")
        private set
    var profitMarginPercentText = MutableStateFlow("100")
        private set
    var printerWattsText = MutableStateFlow("150")
        private set
    var energyCostPerKwhText = MutableStateFlow("0.85")
        private set
    var depreciationPerHourText = MutableStateFlow("1.00")
        private set
    var failureRatePercentText = MutableStateFlow("5")
        private set
    var cashDiscountPercentText = MutableStateFlow("5")
        private set
    var installmentsCount = MutableStateFlow(2)
        private set
    var orderNotes = MutableStateFlow("")
        private set

    // Search and Filters
    var searchQuery = MutableStateFlow("")
        private set
    var selectedStatusFilter = MutableStateFlow<String?>(null)
        private set

    // Calculation result
    private val _calculationResult = MutableStateFlow(CalculationResult())
    val calculationResult: StateFlow<CalculationResult> = _calculationResult.asStateFlow()

    init {
        // Observe settings to initialize defaults
        viewModelScope.launch {
            settings.collect { currentSettings ->
                if (printerWattsText.value == "150") {
                    printerWattsText.value = currentSettings.printerWatts.toInt().toString()
                }
                if (energyCostPerKwhText.value == "0.85") {
                    energyCostPerKwhText.value = currentSettings.energyCostPerKwh.toString()
                }
                if (profitMarginPercentText.value == "100") {
                    profitMarginPercentText.value = currentSettings.defaultProfitMargin.toInt().toString()
                }
                if (depreciationPerHourText.value == "1.00") {
                    depreciationPerHourText.value = currentSettings.defaultDepreciationPerHour.toString()
                }
                if (failureRatePercentText.value == "5") {
                    failureRatePercentText.value = currentSettings.defaultFailureRatePercent.toInt().toString()
                }
                installmentsCount.value = currentSettings.defaultInstallments
                recalculate()
            }
        }

        // Auto select first material when materials loaded
        viewModelScope.launch {
            allMaterials.collect { list ->
                if (selectedMaterial.value == null && list.isNotEmpty()) {
                    val defaultMat = list.firstOrNull { it.isDefault } ?: list.first()
                    selectedMaterial.value = defaultMat
                    customMaterialPriceText.value = defaultMat.pricePerKg.toString()
                    recalculate()
                }
            }
        }
    }

    // Input setters
    fun setClientName(value: String) { clientName.value = value }
    fun setProjectName(value: String) { projectName.value = value }
    fun setOrderNotes(value: String) { orderNotes.value = value }

    fun setQuantity(value: String) {
        quantityText.value = value
        recalculate()
    }

    fun setGrams(value: String) {
        gramsText.value = value
        recalculate()
    }

    fun setIsGramsPerItem(value: Boolean) {
        isGramsPerItem.value = value
        recalculate()
    }

    fun setPrintHours(value: String) {
        printHoursText.value = value
        recalculate()
    }

    fun setPrintMinutes(value: String) {
        printMinutesText.value = value
        recalculate()
    }

    fun setIsTimePerItem(value: Boolean) {
        isTimePerItem.value = value
        recalculate()
    }

    fun selectMaterial(material: MaterialEntity) {
        selectedMaterial.value = material
        customMaterialPriceText.value = material.pricePerKg.toString()
        recalculate()
    }

    fun setCustomMaterialPrice(value: String) {
        customMaterialPriceText.value = value
        recalculate()
    }

    fun setExtraCosts(value: String) {
        extraCostsText.value = value
        recalculate()
    }

    fun setExtraCostsDescription(value: String) {
        extraCostsDescription.value = value
    }

    fun setProfitMarginPercent(value: String) {
        profitMarginPercentText.value = value
        recalculate()
    }

    fun setPrinterWatts(value: String) {
        printerWattsText.value = value
        recalculate()
    }

    fun setEnergyCostPerKwh(value: String) {
        energyCostPerKwhText.value = value
        recalculate()
    }

    fun setDepreciationPerHour(value: String) {
        depreciationPerHourText.value = value
        recalculate()
    }

    fun setFailureRatePercent(value: String) {
        failureRatePercentText.value = value
        recalculate()
    }

    fun setCashDiscountPercent(value: String) {
        cashDiscountPercentText.value = value
        recalculate()
    }

    fun setInstallmentsCount(value: Int) {
        installmentsCount.value = value.coerceIn(1, 12)
        recalculate()
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSelectedStatusFilter(status: String?) {
        selectedStatusFilter.value = status
    }

    // Core Calculation Logic
    fun recalculate() {
        val qty = quantityText.value.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val parsedGrams = gramsText.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val totalGrams = if (isGramsPerItem.value) parsedGrams * qty else parsedGrams
        val gramsPerItem = if (isGramsPerItem.value) parsedGrams else (if (qty > 0) totalGrams / qty else 0.0)

        val matPricePerKg = customMaterialPriceText.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val materialCost = (totalGrams / 1000.0) * matPricePerKg

        // Machine Time & Energy
        val h = printHoursText.value.toDoubleOrNull() ?: 0.0
        val m = printMinutesText.value.toDoubleOrNull() ?: 0.0
        val timeDecimal = (h + (m / 60.0)).coerceAtLeast(0.0)
        val totalMachineHours = if (isTimePerItem.value) timeDecimal * qty else timeDecimal

        val watts = printerWattsText.value.replace(",", ".").toDoubleOrNull() ?: 150.0
        val kwhPrice = energyCostPerKwhText.value.replace(",", ".").toDoubleOrNull() ?: 0.85
        val totalEnergyKwh = (watts / 1000.0) * totalMachineHours
        val energyCost = totalEnergyKwh * kwhPrice

        // Extra Materials / Insumos fora PLA (screws, inserts, resin IPA wash, glue, packaging)
        val extraCosts = extraCostsText.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val depPerHour = depreciationPerHourText.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val totalDepreciation = depPerHour * totalMachineHours

        val failureRate = failureRatePercentText.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val failureCost = (materialCost + energyCost) * (failureRate / 100.0)

        val totalProductionCost = materialCost + energyCost + extraCosts + totalDepreciation + failureCost

        val profitPercent = profitMarginPercentText.value.replace(",", ".").toDoubleOrNull() ?: 100.0
        val totalProfit = totalProductionCost * (profitPercent / 100.0)
        val totalFinalPrice = totalProductionCost + totalProfit
        val unitPrice = if (qty > 0) totalFinalPrice / qty else 0.0

        val discountPercent = cashDiscountPercentText.value.replace(",", ".").toDoubleOrNull() ?: 5.0
        val cashPrice = totalFinalPrice * (1.0 - (discountPercent / 100.0))

        val inst = installmentsCount.value.coerceAtLeast(1)
        val installmentValue = if (inst > 0) totalFinalPrice / inst else totalFinalPrice

        _calculationResult.value = CalculationResult(
            quantity = qty,
            gramsPerItem = gramsPerItem,
            totalGrams = totalGrams,
            filamentCostPerKg = matPricePerKg,
            materialCost = materialCost,
            printHours = totalMachineHours,
            printerWatts = watts,
            energyCostPerKwh = kwhPrice,
            energyKwh = totalEnergyKwh,
            energyCost = energyCost,
            extraCosts = extraCosts,
            failureRatePercent = failureRate,
            failureCost = failureCost,
            depreciationPerHour = depPerHour,
            depreciationCost = totalDepreciation,
            totalProductionCost = totalProductionCost,
            profitPercentage = profitPercent,
            totalProfit = totalProfit,
            totalFinalPrice = totalFinalPrice,
            unitPrice = unitPrice,
            cashDiscountPercent = discountPercent,
            cashPrice = cashPrice,
            installments = inst,
            installmentValue = installmentValue
        )
    }

    // Save order from calculator state
    fun saveCurrentOrder(onSuccess: (Long) -> Unit) {
        val calc = _calculationResult.value
        val cName = clientName.value.ifBlank { "Cliente Avulso" }
        val pName = projectName.value.ifBlank { "Projeto 3D" }
        val matName = selectedMaterial.value?.name ?: "Personalizado"

        val paymentMethod = if (calc.installments > 1) {
            "Parcelado em ${calc.installments}x"
        } else {
            "À vista"
        }

        val order = OrderEntity(
            clientName = cName,
            projectName = pName,
            quantity = calc.quantity,
            materialName = matName,
            filamentGrams = calc.totalGrams,
            filamentCostPerKg = calc.filamentCostPerKg,
            printHours = calc.printHours,
            printerWatts = calc.printerWatts,
            energyCostPerKwh = calc.energyCostPerKwh,
            extraCosts = calc.extraCosts,
            extraCostsDescription = extraCostsDescription.value,
            profitPercentage = calc.profitPercentage,
            totalProductionCost = calc.totalProductionCost,
            totalProfit = calc.totalProfit,
            totalFinalPrice = calc.totalFinalPrice,
            paymentMethod = paymentMethod,
            installments = calc.installments,
            installmentValue = calc.installmentValue,
            status = "ORCAMENTO",
            notes = orderNotes.value
        )

        viewModelScope.launch {
            val id = repository.insertOrder(order)
            onSuccess(id)
        }
    }

    fun updateOrderStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, newStatus)
        }
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            repository.deleteOrder(id)
        }
    }

    fun loadOrderIntoCalculator(order: OrderEntity) {
        clientName.value = order.clientName
        projectName.value = order.projectName
        quantityText.value = order.quantity.toString()
        gramsText.value = if (order.quantity > 0) (order.filamentGrams / order.quantity).toString() else order.filamentGrams.toString()
        isGramsPerItem.value = true

        val totalMinutes = (order.printHours * 60).toInt()
        val perItemMinutes = if (order.quantity > 0) totalMinutes / order.quantity else totalMinutes
        printHoursText.value = (perItemMinutes / 60).toString()
        printMinutesText.value = (perItemMinutes % 60).toString()
        isTimePerItem.value = true

        customMaterialPriceText.value = order.filamentCostPerKg.toString()
        extraCostsText.value = order.extraCosts.toString()
        extraCostsDescription.value = order.extraCostsDescription
        profitMarginPercentText.value = order.profitPercentage.toInt().toString()
        printerWattsText.value = order.printerWatts.toInt().toString()
        energyCostPerKwhText.value = order.energyCostPerKwh.toString()
        installmentsCount.value = order.installments
        orderNotes.value = order.notes

        val match = allMaterials.value.firstOrNull { it.name == order.materialName }
        if (match != null) {
            selectedMaterial.value = match
        }

        recalculate()
    }

    // Material catalog actions
    fun addMaterial(material: MaterialEntity) {
        viewModelScope.launch {
            repository.insertMaterial(material)
        }
    }

    fun updateMaterial(material: MaterialEntity) {
        viewModelScope.launch {
            repository.updateMaterial(material)
        }
    }

    fun deleteMaterial(id: Long) {
        viewModelScope.launch {
            repository.deleteMaterial(id)
        }
    }

    // Settings actions
    fun saveSettings(
        watts: Double,
        kwhCost: Double,
        profitMargin: Double,
        depreciation: Double,
        failureRate: Double,
        installments: Int
    ) {
        viewModelScope.launch {
            val updated = SettingsEntity(
                id = 1,
                printerWatts = watts,
                energyCostPerKwh = kwhCost,
                defaultProfitMargin = profitMargin,
                defaultDepreciationPerHour = depreciation,
                defaultFailureRatePercent = failureRate,
                defaultInstallments = installments
            )
            repository.saveSettings(updated)
        }
    }

    // Quick Reset
    fun resetCalculator() {
        clientName.value = ""
        projectName.value = ""
        quantityText.value = "1"
        gramsText.value = "50"
        printHoursText.value = "2"
        printMinutesText.value = "0"
        extraCostsText.value = "0.00"
        extraCostsDescription.value = ""
        orderNotes.value = ""
        recalculate()
    }
}
