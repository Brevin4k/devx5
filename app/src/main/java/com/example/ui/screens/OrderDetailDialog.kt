package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.OrderEntity
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusSuccess
import com.example.util.FormatUtils

@Composable
fun OrderDetailDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onStatusChange: (String) -> Unit,
    onLoadInCalculator: () -> Unit,
    onDelete: () -> Unit
) {
    val scrollState = rememberScrollState()

    val statusColor = when (order.status) {
        "ORCAMENTO" -> StatusPurple
        "EM_IMPRESSAO" -> OrangeSecondary
        "CONCLUIDO" -> StatusSuccess
        "ENTREGUE" -> StatusInfo
        else -> MaterialTheme.colorScheme.primary
    }

    val statusLabel = when (order.status) {
        "ORCAMENTO" -> "Orçamento"
        "EM_IMPRESSAO" -> "Em Impressão"
        "CONCLUIDO" -> "Concluído"
        "ENTREGUE" -> "Entregue"
        else -> order.status
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("order_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = order.clientName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = order.projectName.ifBlank { "Impressão 3D" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Selector Row
                Text(
                    text = "STATUS DO PEDIDO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf(
                        "ORCAMENTO" to "Orçamento",
                        "EM_IMPRESSAO" to "Imprimindo",
                        "CONCLUIDO" to "Pronto",
                        "ENTREGUE" to "Entregue"
                    )
                    statuses.forEach { (code, title) ->
                        val isSelected = order.status == code
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) statusColor else MaterialTheme.colorScheme.surfaceVariant,
                            onClick = { onStatusChange(code) }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Main Financial Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow("Valor Total do Pedido", FormatUtils.formatCurrency(order.totalFinalPrice), isHighlight = true)
                        DetailRow("Valor por Peça", FormatUtils.formatCurrency(if (order.quantity > 0) order.totalFinalPrice / order.quantity else 0.0))
                        DetailRow("Custo Total de Produção", FormatUtils.formatCurrency(order.totalProductionCost))
                        DetailRow("Lucro Líquido", "${FormatUtils.formatCurrency(order.totalProfit)} (+${FormatUtils.formatDecimal(order.profitPercentage, 0)}%)", valueColor = StatusSuccess)

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))

                        DetailRow("Quantidade de Peças", "${order.quantity} unidades")
                        DetailRow("Material Utilizado", "${order.materialName} (${FormatUtils.formatCurrency(order.filamentCostPerKg)}/kg)")
                        DetailRow("Peso Total de Filamento", "${FormatUtils.formatDecimal(order.filamentGrams, 1)}g (${FormatUtils.formatDecimal(order.filamentGrams / order.quantity.coerceAtLeast(1), 1)}g/peça)")
                        DetailRow("Horas de Máquina / Energia", "${FormatUtils.formatHours(order.printHours)} (${order.printerWatts.toInt()}W)")
                        DetailRow("Tarifa de Energia", "${FormatUtils.formatCurrency(order.energyCostPerKwh)}/kWh")

                        if (order.extraCosts > 0) {
                            DetailRow("Insumos Extras", "${FormatUtils.formatCurrency(order.extraCosts)} (${order.extraCostsDescription.ifBlank { "Parafusos/acabamento" }})")
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))

                        DetailRow(
                            "Condição de Pagamento",
                            if (order.installments > 1) {
                                "${order.installments}x de ${FormatUtils.formatCurrency(order.installmentValue)}"
                            } else {
                                "À vista (PIX / Dinheiro)"
                            }
                        )
                    }
                }

                if (order.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "OBSERVAÇÕES DO CLIENTE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = order.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Compartilhar WhatsApp, Carregar na Calculadora, Excluir
                Button(
                    onClick = onShare,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_whatsapp_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Compartilhar com Cliente", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onLoadInCalculator,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("load_calculator_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Recalcular", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("delete_order_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Excluir", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlight) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isHighlight) CyanPrimary else valueColor
        )
    }
}
