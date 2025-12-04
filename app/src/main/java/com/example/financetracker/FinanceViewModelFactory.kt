package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financetracker.data.TransactionRepository

/**
 * FinanceViewModelFactory - Factory for creating FinanceViewModel with dependencies
 *
 * ViewModelProvider.Factory is required when ViewModel has constructor parameters
 * This allows us to inject the repository into the ViewModel
 *
 * Without this, viewModel() would only work for ViewModels with empty constructors
 */
class FinanceViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    /**
     * Create the ViewModel instance
     *
     * This is called by ViewModelProvider when creating the ViewModel
     * We check if the requested class is FinanceViewModel and create it with the repository

     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
