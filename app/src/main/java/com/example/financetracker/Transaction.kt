package com.example.financetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Transaction Entity - Represents a single transaction in the database
 *
 * @Entity annotation marks this as a Room database table
 * @PrimaryKey marks the id field as the primary key
 *
 * Each property corresponds to a column in the transactions table
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), // Unique identifier for each transaction
    val amount: Double, // Transaction amount
    val type: TransactionType, // INCOME or EXPENSE
    val category: String, // Category name (Food, Transport, etc.)
    val date: Long = System.currentTimeMillis(), // Unix timestamp in milliseconds
    val note: String = "" // Optional note/description
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

// Default categories
object Categories {
    val expenseCategories = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Entertainment",
        "Bills",
        "Other"
    )

    val incomeCategories = listOf(
        "Salary",
        "Gift",
        "Other"
    )
}
