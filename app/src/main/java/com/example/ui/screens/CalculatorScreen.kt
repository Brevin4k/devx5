package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MaterialEntity
import com.example.ui.MainViewModel
import com.example.ui.components.CostSummaryCard
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.util.FormatUtils

@Composable
fun CalculatorScreen(
    viewModel: MainViewModel,
    onOrderSaved: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val clientName by viewModel.clientName.collectAsStateWithLifecycle()
    val projectName by viewModel.projectName.collectAsStateWithLifecycle()
    val quantityText by viewModel.quantityText.collectAsStateWithLifecycle()
    val gramsText by viewModel.gramsText.collectAsStateWithLifecycle()
    val isGramsPerItem by viewModel.isGramsPerItem.collectAsStateWithLifecycle()

    val printHoursText by viewModel.printHoursText.collectAsStateWithLifecycle()
    val printMinutesText by viewModel.printMinutesText.collectAsStateWithLifecycle()
    val isTimePerItem by viewModel.isTimePerItem.collectAsStateWithLifecycle()

    val allMaterials by viewModel.allMaterials.collectAsStateWithLifecycle()
    val selectedMaterial by viewModel.selectedMaterial.collectAsStateWithLifecycle()
    val customMaterialPriceText by viewModel.customMaterialPriceText.collectAsStateWithLifecycle()

    val extraCostsText by viewModel.extraCostsText.collectAsStateWithLifecycle()
    val extraCostsDescription by viewModel.extraCostsDescription.collectAsStateWithLifecycle()

    val profitMarginPercentText by viewModel.profitMarginPercentText.collectAsStateWithLifecycle()
    val printerWattsText by viewModel.printerWattsText.collectAsStateWithLifecycle()
    val energyCostPerKwhText by viewModel.energyCostPerKwhText.collectAsStateWithLifecycle()
    val installmentsCount by viewModel.installmentsCount.collectAsStateWithLifecycle()
    val orderNotes by viewModel.orderNotes.collectAsStateWithLifecycle()

    val calculation by viewModel.calculationResult.collectAsStateWithLifecycle()

    var showAdvancedEnergy by remember { mutableStateOf(false) }
    var showExtraMaterialsSection by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Real-time Visual Summary Card at Top
        CostSummaryCard(calculation = calculation)

        // Section 1: Cliente e Lançamento
        SectionCard(
            title = "1. Cliente & Lançamento",
            icon = Icons.Default.Person,
            tag = "section_client"
        ) {
            OutlinedTextField(
                value = clientName,
                onValueChange = { viewModel.setClientName(it) },
                label = { Text("Nome do Cliente (ex: Lukinha)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = projectName,
                onValueChange = { viewModel.setProjectName(it) },
                label = { Text("Nome da Peça / Projeto (ex: Action Figure, Suporte)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity with stepper buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quantidade de peças:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Total a ser produzido no lote",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            if (current > 1) viewModel.setQuantity((current - 1).toString())
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("quantity_decrement_button")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Diminuir")
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { viewModel.setQuantity(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(70.dp)
                            .testTag("quantity_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            viewModel.setQuantity((current + 1).toString())
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("quantity_increment_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
            }
        }

        // Section 2: Filamento & Material
        SectionCard(
            title = "2. Consumo de Material",
            icon = Icons.Default.Layers,
            tag = "section_material"
        ) {
            Text(
                text = "Selecione o Material:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Scrollable Material Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allMaterials.forEach { mat ->
                    val isSelected = selectedMaterial?.id == mat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectMaterial(mat) },
                        label = { Text("${mat.name} (${mat.type})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                            selectedLabelColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("material_chip_${mat.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filament price per kg
            OutlinedTextField(
                value = customMaterialPriceText,
                onValueChange = { viewModel.setCustomMaterialPrice(it) },
                label = { Text("Preço do kg do Material (R$/kg)") },
                prefix = { Text("R$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_price_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Grams & Per Item toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = gramsText,
                    onValueChange = { viewModel.setGrams(it) },
                    label = { Text(if (isGramsPerItem) "Gramas por peça" else "Gramas totais") },
                    suffix = { Text("g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("grams_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.setIsGramsPerItem(!isGramsPerItem) }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isGramsPerItem) "Por Peça" else "Lote Total",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                        Text(
                            text = "Toque p/ alternar",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section 3: Energia & Tempo de Máquina
        SectionCard(
            title = "3. Energia & Horas de Máquina",
            icon = Icons.Default.Bolt,
            tag = "section_energy"
        ) {
            // Hours and Minutes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = printHoursText,
                    onValueChange = { viewModel.setPrintHours(it) },
                    label = { Text("Horas") },
                    suffix = { Text("h") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("print_hours_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = printMinutesText,
                    onValueChange = { viewModel.setPrintMinutes(it) },
                    label = { Text("Minutos") },
                    suffix = { Text("min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("print_minutes_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.setIsTimePerItem(!isTimePerItem) }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isTimePerItem) "Por Peça" else "Lote Total",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OrangeSecondary
                        )
                        Text(
                            text = "Toque p/ alternar",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Advanced Energy settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedEnergy = !showAdvancedEnergy }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OrangeSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Potência (${printerWattsText}W) & Tarifa (R$ ${energyCostPerKwhText}/kWh)",
                        style = MaterialTheme.typography.labelMedium,
                        color = OrangeSecondary
                    )
                }
                Icon(
                    imageVector = if (showAdvancedEnergy) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = OrangeSecondary
                )
            }

            AnimatedVisibility(visible = showAdvancedEnergy) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = printerWattsText,
                        onValueChange = { viewModel.setPrinterWatts(it) },
                        label = { Text("Potência Média da Impressora") },
                        suffix = { Text("Watts") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = energyCostPerKwhText,
                        onValueChange = { viewModel.setEnergyCostPerKwh(it) },
                        label = { Text("Custo da Energia por kWh") },
                        prefix = { Text("R$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Section 4: Materiais Fora PLA & Insumos Extras
        SectionCard(
            title = "4. Insumos Fora PLA & Acabamento",
            icon = Icons.Default.Category,
            tag = "section_extra_materials"
        ) {
            Text(
                text = "Parafusos, insertos de latão, cola, verniz, primer, álcool isopropílico para resina, caixas ou embalagem.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = extraCostsText,
                onValueChange = { viewModel.setExtraCosts(it) },
                label = { Text("Custo de Insumos Adicionais") },
                prefix = { Text("R$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("extra_costs_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = extraCostsDescription,
                onValueChange = { viewModel.setExtraCostsDescription(it) },
                label = { Text("Descrição dos Insumos (opcional)") },
                placeholder = { Text("ex: 4 parafusos M3, 2 insertos, álcool") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("extra_costs_desc_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Section 5: Lucro & Parcelamento
        SectionCard(
            title = "5. Margem de Lucro & Parcelamento",
            icon = Icons.Default.TrendingUp,
            tag = "section_profit"
        ) {
            Text(
                text = "Margem de Lucro Desejada:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Preset profit margin chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("50", "80", "100", "150", "200").forEach { margin ->
                    val isSelected = profitMarginPercentText == margin
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setProfitMarginPercent(margin) },
                        label = { Text("+$margin%") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = profitMarginPercentText,
                onValueChange = { viewModel.setProfitMarginPercent(it) },
                label = { Text("Margem de Lucro (%)") },
                suffix = { Text("%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profit_margin_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Installments selection
            Text(
                text = "Opções de Parcelamento para o Cliente:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1 to "À vista", 2 to "2x", 3 to "3x", 4 to "4x", 6 to "6x").forEach { (num, label) ->
                    val isSelected = installmentsCount == num
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setInstallmentsCount(num) },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = orderNotes,
                onValueChange = { viewModel.setOrderNotes(it) },
                label = { Text("Observações do Pedido (opcional)") },
                placeholder = { Text("ex: 20% infill, cor azul petróleo, entrega sexta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_notes_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Action Buttons: Lançar Pedido & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.resetCalculator() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("reset_calculator_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Limpar")
            }

            Button(
                onClick = {
                    viewModel.saveCurrentOrder { orderId ->
                        onOrderSaved(orderId)
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("save_order_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lançar Pedido do Cliente",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}
