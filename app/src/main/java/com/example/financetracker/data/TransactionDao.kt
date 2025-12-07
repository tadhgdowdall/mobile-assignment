package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financetracker.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * TransactionDao - Data Access Object for Transaction operations
 *
 * @Dao annotation tells Room this is a Database Access Object
 * Contains all CRUD (Create, Read, Update, Delete) operations
 *
 * Flow is used for reactive database queries - automatically updates UI when data changes
 */
@Dao
interface TransactionDao {

    /**
     * CREATE - Insert a new transaction
     *
     * @Insert annotation generates the SQL INSERT statement
     * OnConflictStrategy.REPLACE means if transaction with same ID exists, replace it
     *
     * suspend keyword means this runs on a background thread (coroutine)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    /**
     * CREATE - Insert multiple transactions at once
     * Useful for initial data setup or batch operations
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    /**
     * UPDATE - Update an existing transaction
     *
     * @Update annotation generates the SQL UPDATE statement
     * Matches transaction by primary key (id)
     */
    @Update
    suspend fun update(transaction: Transaction)

    /**
     * DELETE - Delete a transaction
     *
     * @Delete annotation generates the SQL DELETE statement
     * Matches transaction by primary key (id)
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * DELETE - Delete transaction by ID
     *
     * @Query lets us write custom SQL
     * :transactionId is a parameter placeholder
     */
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteById(transactionId: String)

    /**
     * READ - Get all transactions ordered by date (newest first)
     *
     * Returns Flow<List<Transaction>> - automatically updates when database changes
     * Flow is reactive - UI will update automatically when data changes
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * READ - Get a single transaction by ID
     *
     * Returns Flow so UI can react to changes
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionById(transactionId: String): Flow<Transaction?>

    /**
     * READ - Get transactions by type (INCOME or EXPENSE)
     *
     * This allows filtering transactions in the UI
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    /**
     * READ - Get transactions by category
     *
     * Useful for budget tracking and spending analysis
     */
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    /**
     * DELETE - Delete all transactions
     *
     * Useful for testing or resetting the app
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    /**
     * READ - Get transactions within a date range
     *
     * Used for budget checking and reports
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getTransactionsInDateRange(startDate: Long, endDate: Long): List<Transaction>
}
