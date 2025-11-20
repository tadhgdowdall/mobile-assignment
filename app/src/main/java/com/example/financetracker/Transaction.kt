package com.example.financetracker

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

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
