package com.example.financetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val transaction = viewModel.getTransaction(transactionId)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // Editable state
    var editAmount by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }

    if (transaction == null) {
        // Transaction not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Transaction not found")
        }
        return
    }

    // Initialize edit fields
    LaunchedEffect(transaction) {
        editAmount = transaction.amount.toString()
        editNote = transaction.note
    }

    val dateFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.date))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Transaction" else "Transaction Details") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        TextButton(
                            onClick = {
                                // Save changes
                                val newAmount = editAmount.toDoubleOrNull()
                                if (newAmount != null && newAmount > 0) {
                                    val updatedTransaction = transaction.copy(
                                        amount = newAmount,
                                        note = editNote
                                    )
                                    viewModel.updateTransaction(updatedTransaction)
                                    isEditing = false
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    } else {
                        TextButton(onClick = { isEditing = true }) {
                            Text("Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Amount Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (transaction.type == TransactionType.INCOME)
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else
                        Color(0xFFF44336).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (transaction.type == TransactionType.INCOME) "Income" else "Expense",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editAmount,
                            onValueChange = { editAmount = it },
                            label = { Text("Amount (€)") },
                            textStyle = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "€%.2f".format(transaction.amount),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.type == TransactionType.INCOME)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFF44336)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details
            DetailRow(label = "Category", value = transaction.category)
            Spacer(modifier = Modifier.height(16.dp))
            DetailRow(label = "Date", value = formattedDate)

            Spacer(modifier = Modifier.height(16.dp))

            // Note field - editable
            if (isEditing) {
                OutlinedTextField(
                    value = editNote,
                    onValueChange = { editNote = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            } else if (transaction.note.isNotBlank()) {
                DetailRow(label = "Note", value = transaction.note)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Delete Button
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("Delete Transaction")
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transactionId)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
