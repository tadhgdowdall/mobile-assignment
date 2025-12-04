package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * FinanceViewModel - Manages UI state and business logic
 *
 * ViewModelScope ensures coroutines are cancelled when ViewModel is cleared
 * This prevents memory leaks and unnecessary background work
 *
 * @param repository The repository that handles data operations
 */
class FinanceViewModel(private val repository: TransactionRepository) : ViewModel() {

    /**
     * StateFlow of all transactions from the database
     * This automatically updates when the database changes
     */
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    /**
     * StateFlow of current balance (income - expenses)
     */
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    init {
        // Collect transactions from repository when ViewModel is created
        // viewModelScope.launch runs a coroutine tied to the ViewModel lifecycle
        viewModelScope.launch {
            repository.allTransactions.collect { transactionList ->
                _transactions.value = transactionList
                updateBalance()
            }
        }
    }

    /**
     * CREATE - Add a new transaction to the database
     *
     * viewModelScope.launch creates a coroutine for the suspend function
     * This runs on a background thread and doesn't block the UI
     *
     * @param transaction The transaction to add
     */
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
            // No need to manually update _transactions - the Flow will update automatically
        }
    }

    /**
     * UPDATE - Update an existing transaction
     *
     * @param transaction The transaction with updated values
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.update(transaction)
            // Flow will update automatically
        }
    }

    /**
     * DELETE - Delete a transaction by ID
     *
     * @param transactionId The ID of the transaction to delete
     */
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.deleteById(transactionId)
            // Flow will update automatically
        }
    }

    /**
     * READ - Get a single transaction by ID
     *
     * This searches the current in-memory list
     * For real-time updates, you could collect from repository.getTransactionById()
     *
     * @param id The transaction ID
     * @return The transaction if found, null otherwise
     */
    fun getTransaction(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }

    /**
     * Calculate and update the balance
     *
     * This is called automatically when transactions change
     * Balance = Total Income - Total Expenses
     */
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
