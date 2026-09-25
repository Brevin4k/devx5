package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.MaterialsScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.util.FormatUtils
import kotlinx.coroutines.launch

enum class AppDestination(val title: String, val icon: ImageVector, val tag: String) {
    CALCULATOR("Calculadora", Icons.Default.Calculate, "tab_calculator"),
    ORDERS("Clientes & Pedidos", Icons.Default.Assignment, "tab_orders"),
    MATERIALS("Materiais & Ajustes", Icons.Default.Tune, "tab_materials")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintCalcApp(
    viewModel: MainViewModel = viewModel()
) {
    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val destinations = AppDestination.entries.toTypedArray()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "PrintCalc 3D",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (!isTablet) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    destinations.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.surface,
                                indicatorColor = CyanPrimary
                            ),
                            modifier = Modifier.testTag(destination.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    destinations.forEachIndexed { index, destination ->
                        NavigationRailItem(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.surface,
                                indicatorColor = CyanPrimary
                            ),
                            modifier = Modifier.testTag(destination.tag)
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    TabContent(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        onNavigateToOrders = { currentTab = 1 },
                        onNavigateToCalculator = { currentTab = 0 },
                        onOrderSaved = { orderId ->
                            currentTab = 1
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Pedido lançado com sucesso!",
                                    actionLabel = "Compartilhar",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    val order = viewModel.allOrders.value.firstOrNull { it.id == orderId }
                                    if (order != null) {
                                        FormatUtils.shareOrder(context, order)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TabContent(
                    currentTab = currentTab,
                    viewModel = viewModel,
                    onNavigateToOrders = { currentTab = 1 },
                    onNavigateToCalculator = { currentTab = 0 },
                    onOrderSaved = { orderId ->
                        currentTab = 1
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Pedido lançado com sucesso!",
                                actionLabel = "Compartilhar",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                val order = viewModel.allOrders.value.firstOrNull { it.id == orderId }
                                if (order != null) {
                                    FormatUtils.shareOrder(context, order)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TabContent(
    currentTab: Int,
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onOrderSaved: (Long) -> Unit
) {
    when (currentTab) {
        0 -> CalculatorScreen(
            viewModel = viewModel,
            onOrderSaved = onOrderSaved
        )
        1 -> OrdersScreen(
            viewModel = viewModel,
            onNavigateToCalculator = onNavigateToCalculator
        )
        2 -> MaterialsScreen(
            viewModel = viewModel
        )
    }
}
