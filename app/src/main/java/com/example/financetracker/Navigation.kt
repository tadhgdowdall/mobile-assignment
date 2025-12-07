package com.example.financetracker

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object Camera : Screen("camera")
    object TransactionList : Screen("transaction_list")
    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }
    object Budget : Screen("budget")
    object Settings : Screen("settings")
}
