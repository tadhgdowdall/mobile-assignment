package com.example.financetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.Categories
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.Transaction
import com.example.financetracker.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()

    // Simple fixed budget of 250 per category
    val budgetPerCategory = 250.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Budget Overview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToTransactions,
                    icon = { Text("$", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    label = { Text("Transactions") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("💰", fontSize = 20.sp) },
                    label = { Text("Budget") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Monthly Budget per Category",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(Categories.expenseCategories) { category ->
                val spent = calculateCategorySpent(transactions, category)
                BudgetCategoryCard(
                    category = category,
                    spent = spent,
                    budget = budgetPerCategory
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BudgetCategoryCard(
    category: String,
    spent: Double,
    budget: Double
) {
    val progress = (spent / budget).coerceIn(0.0, 1.0).toFloat()
    val remaining = budget - spent

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "€${String.format("%.0f", budget)}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    progress > 0.9 -> Color(0xFFF44336) // Red
                    progress > 0.7 -> Color(0xFFFF9800) // Orange
                    else -> MaterialTheme.colorScheme.primary // Blue
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: €${String.format("%.2f", spent)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (remaining >= 0)
                        "Remaining: €${String.format("%.2f", remaining)}"
                    else
                        "Over budget: €${String.format("%.2f", -remaining)}",
                    fontSize = 14.sp,
                    color = if (remaining >= 0)
                        Color(0xFF4CAF50) // Green
                    else
                        Color(0xFFF44336), // Red
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun calculateCategorySpent(transactions: List<Transaction>, category: String): Double {
    return transactions
        .filter { it.type == TransactionType.EXPENSE && it.category == category }
        .sumOf { it.amount }
}
