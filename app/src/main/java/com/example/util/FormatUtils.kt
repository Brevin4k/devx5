package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.OrderEntity
import com.example.model.CalculationResult
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val ptBrLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)

    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }

    fun formatDecimal(value: Double, decimals: Int = 2): String {
        return String.format(ptBrLocale, "%.${decimals}f", value)
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", ptBrLocale)
        return sdf.format(Date(timestamp))
    }

    fun formatHours(hoursDecimal: Double): String {
        val h = hoursDecimal.toInt()
        val m = ((hoursDecimal - h) * 60).toInt()
        return if (h > 0 && m > 0) {
            "${h}h ${m}min"
        } else if (h > 0) {
            "${h}h"
        } else {
            "${m}min"
        }
    }

    fun generateShareText(order: OrderEntity): String {
        val totalPerItem = if (order.quantity > 0) order.totalFinalPrice / order.quantity else order.totalFinalPrice
        val gramsPerItem = if (order.quantity > 0) order.filamentGrams / order.quantity else order.filamentGrams

        val paymentText = if (order.installments > 1) {
            "• À vista (PIX): ${formatCurrency(order.totalFinalPrice)}\n• Parcelado: ${order.installments}x de ${formatCurrency(order.installmentValue)}"
        } else {
            "• Pagamento à vista: ${formatCurrency(order.totalFinalPrice)}"
        }

        return """
🖨️ *ORÇAMENTO DE IMPRESSÃO 3D*
━━━━━━━━━━━━━━━━━━━━
👤 *Cliente:* ${order.clientName}
📦 *Projeto:* ${order.projectName.ifBlank { "Impressão Sob Demanda" }}
🔢 *Quantidade:* ${order.quantity} ${if (order.quantity == 1) "peça" else "peças"}
🧵 *Material:* ${order.materialName}
⚖️ *Peso Total:* ${formatDecimal(order.filamentGrams, 1)}g (${formatDecimal(gramsPerItem, 1)}g por peça)
⏱️ *Tempo de Impressão:* ${formatHours(order.printHours)}
⚡ *Energia:* ${formatHours(order.printHours)} em máquina (${order.printerWatts.toInt()}W)
━━━━━━━━━━━━━━━━━━━━
💰 *VALOR TOTAL:* ${formatCurrency(order.totalFinalPrice)}
💵 *Valor Unitário:* ${formatCurrency(totalPerItem)} / peça

💳 *Formas de Pagamento:*
$paymentText
${if (order.notes.isNotBlank()) "\n📝 *Obs:* ${order.notes}" else ""}
━━━━━━━━━━━━━━━━━━━━
Orçamento gerado pelo PrintCalc 3D
        """.trimIndent()
    }

    fun shareOrder(context: Context, order: OrderEntity) {
        val text = generateShareText(order)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Enviar orçamento para ${order.clientName}")
        context.startActivity(shareIntent)
    }
}
