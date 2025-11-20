package com.example.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.MobileDevelopmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileDevelopmentTheme {
                Surface {
                    FinanceApp()
                }
            }
        }
    }
}

@Composable
fun FinanceApp() {

    // List of expenses: use val because the reference doesn't change
    val expenses = remember { mutableStateListOf<Pair<String, Double>>() }

    // Input fields: must be var because they are modified
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    // Calculate total spent
    val total = expenses.sumOf { it.second }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Expense Tracker", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Spent: €$total", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Expense Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Amount input
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (€)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Add expense button
        Button(
            onClick = {
                if (name.isNotBlank() && amount.isNotBlank()) {
                    expenses.add(name to amount.toDouble())
                    name = ""
                    amount = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Your Expenses:", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // Display expenses list
        LazyColumn {
            items(expenses) { item ->
                Text("${item.first}: €${item.second}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceAppPreview() {
    MobileDevelopmentTheme {
        FinanceApp()
    }
}
