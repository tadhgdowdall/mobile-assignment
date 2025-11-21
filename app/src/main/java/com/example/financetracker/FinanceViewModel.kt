package com.example.financetracker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FinanceViewModel : ViewModel() {

    //Managing all transactions
    // Calculates balance
    //Add, update, delete, and get transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
        updateBalance()
    }

    fun updateTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value.map {
            if (it.id == transaction.id) transaction else it
        }
        updateBalance()
    }

    fun deleteTransaction(transactionId: String) {
        _transactions.value = _transactions.value.filter { it.id != transactionId }
        updateBalance()
    }

    fun getTransaction(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }

    private fun updateBalance() {
        val income = _transactions.value
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = _transactions.value
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        _balance.value = income - expenses
    }
}
