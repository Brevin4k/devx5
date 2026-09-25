package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.OrderEntity
import com.example.ui.MainViewModel
import com.example.ui.components.OrderCard
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.StatusSuccess
import com.example.util.FormatUtils

@Composable
fun OrdersScreen(
    viewModel: MainViewModel,
    onNavigateToCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }

    // Filtered orders
    val filteredOrders by remember(allOrders, searchQuery, selectedStatusFilter) {
        derivedStateOf {
            allOrders.filter { order ->
                val matchesSearch = searchQuery.isBlank() ||
                        order.clientName.contains(searchQuery, ignoreCase = true) ||
                        order.projectName.contains(searchQuery, ignoreCase = true) ||
                        order.materialName.contains(searchQuery, ignoreCase = true)

                val matchesStatus = selectedStatusFilter == null || order.status == selectedStatusFilter
                matchesSearch && matchesStatus
            }
        }
    }

    // Financial KPI totals
    val totalRevenue by remember(allOrders) {
        derivedStateOf { allOrders.sumOf { it.totalFinalPrice } }
    }
    val totalProfit by remember(allOrders) {
        derivedStateOf { allOrders.sumOf { it.totalProfit } }
    }
    val totalPieces by remember(allOrders) {
        derivedStateOf { allOrders.sumOf { it.quantity } }
    }
    val totalMachineHours by remember(allOrders) {
        derivedStateOf { allOrders.sumOf { it.printHours } }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("orders_list"),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Header & Financial KPI Summary Cards
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lançamentos de Clientes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Gerenciamento de pedidos, orçamentos e lucros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dashboard stats cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Faturado",
                            value = FormatUtils.formatCurrency(totalRevenue),
                            icon = Icons.Default.MonetizationOn,
                            tint = CyanPrimary
                        )

                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Lucro Total",
                            value = FormatUtils.formatCurrency(totalProfit),
                            icon = Icons.Default.TrendingUp,
                            tint = StatusSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Peças Impressas",
                            value = "$totalPieces un",
                            icon = Icons.Default.Layers,
                            tint = OrangeSecondary
                        )

                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Horas Máquina",
                            value = FormatUtils.formatHours(totalMachineHours),
                            icon = Icons.Default.Bolt,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_order_input"),
                        placeholder = { Text("Buscar cliente (ex: Lukinha)...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar busca")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips (Todos, Orçamento, Em Impressão, Concluído, Entregue)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatusFilter == null,
                            onClick = { viewModel.setSelectedStatusFilter(null) },
                            label = { Text("Todos (${allOrders.size})") }
                        )

                        val statusOptions = listOf(
                            "ORCAMENTO" to "Orçamentos",
                            "EM_IMPRESSAO" to "Em Impressão",
                            "CONCLUIDO" to "Concluídos",
                            "ENTREGUE" to "Entregues"
                        )

                        statusOptions.forEach { (code, label) ->
                            val count = allOrders.count { it.status == code }
                            FilterChip(
                                selected = selectedStatusFilter == code,
                                onClick = {
                                    viewModel.setSelectedStatusFilter(if (selectedStatusFilter == code) null else code)
                                },
                                label = { Text("$label ($count)") }
                            )
                        }
                    }
                }
            }

            // Orders list items
            if (filteredOrders.isEmpty()) {
                item {
                    EmptyOrdersView(
                        onNewOrderClick = onNavigateToCalculator,
                        isFiltered = searchQuery.isNotBlank() || selectedStatusFilter != null
                    )
                }
            } else {
                items(
                    items = filteredOrders,
                    key = { it.id }
                ) { order ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        OrderCard(
                            order = order,
                            onClick = { selectedOrderForDetail = order },
                            onShare = { FormatUtils.shareOrder(context, order) },
                            onStatusChange = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                            },
                            onDelete = { viewModel.deleteOrder(order.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add New Order via Calculator
        FloatingActionButton(
            onClick = onNavigateToCalculator,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_new_order"),
            containerColor = CyanPrimary,
            contentColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Lançamento")
        }
    }

    // Detail Dialog
    selectedOrderForDetail?.let { order ->
        OrderDetailDialog(
            order = order,
            onDismiss = { selectedOrderForDetail = null },
            onShare = {
                FormatUtils.shareOrder(context, order)
            },
            onStatusChange = { newStatus ->
                viewModel.updateOrderStatus(order.id, newStatus)
                selectedOrderForDetail = order.copy(status = newStatus)
            },
            onLoadInCalculator = {
                viewModel.loadOrderIntoCalculator(order)
                selectedOrderForDetail = null
                onNavigateToCalculator()
            },
            onDelete = {
                viewModel.deleteOrder(order.id)
                selectedOrderForDetail = null
            }
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyOrdersView(
    onNewOrderClick: () -> Unit,
    isFiltered: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = CyanPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isFiltered) "Nenhum pedido encontrado" else "Nenhum pedido lançado ainda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isFiltered) "Tente buscar por outro termo ou limpe os filtros." else "Calcule os custos na aba Calculadora e lance seu primeiro cliente!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (!isFiltered) {
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyanPrimary,
                onClick = onNewOrderClick
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Criar Lançamento",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}
