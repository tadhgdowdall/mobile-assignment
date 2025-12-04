package com.example.financetracker.data

import com.example.financetracker.Transaction
import com.example.financetracker.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * TransactionRepository - Acts as a single source of truth for transaction data
 *
 * Repository Pattern Benefits:
 * - Abstracts data sources from ViewModel
 * - Can easily switch between local/remote data sources
 * - Makes testing easier (can mock the repository)
 * - Centralizes data operations
 *
 * @param transactionDao The DAO for database operations
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    /**
     * READ - Get all transactions as a Flow
     *
     * Flow automatically updates observers when database changes
     * This is exposed to the ViewModel which exposes it to the UI
     */
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    /**
     * CREATE - Insert a new transaction
     *
     * suspend means this must be called from a coroutine
     * The DAO handles the database operation on a background thread
     *
     * @param transaction The transaction to insert
     */
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    /**
     * CREATE - Insert multiple transactions
     *
     * @param transactions List of transactions to insert
     */
    suspend fun insertAll(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    /**
     * UPDATE - Update an existing transaction
     *
     * @param transaction The transaction with updated values
     */
    suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    /**
     * DELETE - Delete a transaction by the object
     *
     * @param transaction The transaction to delete
     */
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    /**
     * DELETE - Delete a transaction by ID
     *
     * More convenient when you only have the ID
     *
     * @param transactionId The ID of the transaction to delete
     */
    suspend fun deleteById(transactionId: String) {
        transactionDao.deleteById(transactionId)
    }

    /**
     * READ - Get a single transaction by ID
     *
     * @param transactionId The transaction ID
     * @return Flow of the transaction (null if not found)
     */
    fun getTransactionById(transactionId: String): Flow<Transaction?> {
        return transactionDao.getTransactionById(transactionId)
    }

    /**
     * READ - Get transactions filtered by type
     *
     * @param type The transaction type (INCOME or EXPENSE)
     * @return Flow of filtered transactions
     */
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name)
    }

    /**
     * READ - Get transactions filtered by category
     *
     * @param category The category name
     * @return Flow of filtered transactions
     */
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    /**
     * DELETE - Delete all transactions
     *
     * Useful for clearing data or testing
     */
    suspend fun deleteAll() {
        transactionDao.deleteAll()
    }
}
