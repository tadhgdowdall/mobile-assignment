package com.example.financetracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.financetracker.data.TransactionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for FinanceViewModel
 *
 * Tests cover:
 * - State initialization
 * - Transaction CRUD operations
 * - Balance calculation
 * - StateFlow updates
 */
@ExperimentalCoroutinesApi
class FinanceViewModelTest {

    // Ensures LiveData/StateFlow updates happen synchronously for testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: FinanceViewModel

    // Sample test data
    private val testTransaction1 = Transaction(
        id = "test-1",
        amount = 100.0,
        type = TransactionType.EXPENSE,
        category = "Food",
        date = System.currentTimeMillis(),
        note = "Test expense"
    )

    private val testTransaction2 = Transaction(
        id = "test-2",
        amount = 500.0,
        type = TransactionType.INCOME,
        category = "Salary",
        date = System.currentTimeMillis(),
        note = "Test income"
    )

    @Before
    fun setup() {
        // Set up test dispatcher for coroutines
        Dispatchers.setMain(testDispatcher)

        // Create mock repository
        repository = mockk(relaxed = true)

        // Mock repository to return empty flow initially
        every { repository.allTransactions } returns flowOf(emptyList())

        // Create ViewModel with mocked repository
        viewModel = FinanceViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test: ViewModel initializes with empty state
     */
    @Test
    fun `initial state should be empty transactions and zero balance`() = runTest {
        assertEquals(emptyList<Transaction>(), viewModel.transactions.first())
        assertEquals(0.0, viewModel.balance.first(), 0.01)
    }

    /**
     * Test: Adding a transaction calls repository insert
     */
    @Test
    fun `addTransaction should call repository insert`() = runTest {
        // When
        viewModel.addTransaction(testTransaction1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.insert(testTransaction1) }
    }

    /**
     * Test: Updating a transaction calls repository update
     */
    @Test
    fun `updateTransaction should call repository update`() = runTest {
        // When
        viewModel.updateTransaction(testTransaction1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.update(testTransaction1) }
    }

    /**
     * Test: Deleting a transaction calls repository delete
     */
    @Test
    fun `deleteTransaction should call repository deleteById`() = runTest {
        // When
        viewModel.deleteTransaction("test-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.deleteById("test-1") }
    }

    /**
     * Test: Balance calculation with income and expenses
     */
    @Test
    fun `balance should be calculated correctly from transactions`() = runTest {
        // Given - repository returns transactions with income and expense
        every { repository.allTransactions } returns flowOf(
            listOf(testTransaction1, testTransaction2)
        )

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - balance should be income (500) - expense (100) = 400
        assertEquals(400.0, newViewModel.balance.first(), 0.01)
    }

    /**
     * Test: Balance with only expenses
     */
    @Test
    fun `balance should be negative with only expenses`() = runTest {
        // Given
        val expense1 = testTransaction1.copy(id = "e1", amount = 100.0)
        val expense2 = testTransaction1.copy(id = "e2", amount = 50.0)

        every { repository.allTransactions } returns flowOf(listOf(expense1, expense2))

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - balance should be -150
        assertEquals(-150.0, newViewModel.balance.first(), 0.01)
    }

    /**
     * Test: Balance with only income
     */
    @Test
    fun `balance should be positive with only income`() = runTest {
        // Given
        val income1 = testTransaction2.copy(id = "i1", amount = 1000.0)
        val income2 = testTransaction2.copy(id = "i2", amount = 500.0)

        every { repository.allTransactions } returns flowOf(listOf(income1, income2))

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - balance should be 1500
        assertEquals(1500.0, newViewModel.balance.first(), 0.01)
    }

    /**
     * Test: Transactions StateFlow updates when repository emits new data
     */
    @Test
    fun `transactions should update when repository emits new data`() = runTest {
        // Given
        every { repository.allTransactions } returns flowOf(
            listOf(testTransaction1)
        )

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val transactions = newViewModel.transactions.first()
        assertEquals(1, transactions.size)
        assertEquals("test-1", transactions[0].id)
    }

    /**
     * Test: Get transaction by ID returns correct transaction
     */
    @Test
    fun `getTransaction should return correct transaction when found`() = runTest {
        // Given
        every { repository.allTransactions } returns flowOf(
            listOf(testTransaction1, testTransaction2)
        )

        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = newViewModel.getTransaction("test-1")

        // Then
        assertNotNull(result)
        assertEquals("test-1", result!!.id)
        assertEquals(100.0, result.amount, 0.01)
    }

    /**
     * Test: Get transaction by ID returns null when not found
     */
    @Test
    fun `getTransaction should return null when transaction not found`() = runTest {
        // Given
        every { repository.allTransactions } returns flowOf(
            listOf(testTransaction1)
        )

        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = newViewModel.getTransaction("non-existent-id")

        // Then
        assertNull(result)
    }

    /**
     * Test: Multiple transactions of different categories
     */
    @Test
    fun `transactions should handle multiple categories correctly`() = runTest {
        // Given
        val foodExpense = testTransaction1.copy(id = "1", category = "Food", amount = 50.0)
        val transportExpense = testTransaction1.copy(id = "2", category = "Transport", amount = 30.0)
        val salaryIncome = testTransaction2.copy(id = "3", category = "Salary", amount = 1000.0)

        every { repository.allTransactions } returns flowOf(
            listOf(foodExpense, transportExpense, salaryIncome)
        )

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val transactions = newViewModel.transactions.first()
        assertEquals(3, transactions.size)

        // Balance: 1000 - 50 - 30 = 920
        assertEquals(920.0, newViewModel.balance.first(), 0.01)
    }

    /**
     * Test: Empty transactions list results in zero balance
     */
    @Test
    fun `empty transactions should result in zero balance`() = runTest {
        // Given
        every { repository.allTransactions } returns flowOf(emptyList())

        // When
        val newViewModel = FinanceViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(0.0, newViewModel.balance.first(), 0.01)
    }
}
