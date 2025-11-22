package com.example.financetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.Transaction
import com.example.financetracker.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onAddTransaction: () -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Filter transactions based on search query
    val filteredTransactions = remember(transactions, searchQuery) {
        if (searchQuery.isBlank()) {
            transactions.reversed()
        } else {
            transactions.reversed().filter { transaction ->
                transaction.category.contains(searchQuery, ignoreCase = true) ||
                transaction.note.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search Transactions") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true
            )

            // Transaction List
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No transactions yet" else "No matching transactions",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        TransactionListItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.date))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Pale card background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.category,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}€%.2f".format(transaction.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME)
                    Color(0xFF4CAF50)
                else
                    Color(0xFFF44336)
            )
        }
    }
}
