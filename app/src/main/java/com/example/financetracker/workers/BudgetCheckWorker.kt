package com.example.financetracker.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.financetracker.R
import com.example.financetracker.TransactionType
import com.example.financetracker.data.AppDatabase

class BudgetCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.transactionDao()

        // Get today's start time (midnight)
        val todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000)

        // Get all transactions from today
        val todayTransactions = dao.getTransactionsInDateRange(todayStart, System.currentTimeMillis())

        // Calculate spending per category
        val categorySpending = todayTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Budget limit per category
        val budgetLimit = 250.0

        // Find categories over budget
        val overBudgetCategories = categorySpending.filter { it.value > budgetLimit }

        // Send notification if any category is over budget
        if (overBudgetCategories.isNotEmpty()) {
            sendNotification(overBudgetCategories, budgetLimit)
        } else {
            // Send test notification to confirm worker is running
            sendTestNotification(todayTransactions.size, categorySpending)
        }

        return Result.success()
    }

    private fun sendNotification(overBudget: Map<String, Double>, limit: Double) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_alerts",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification message
        val category = overBudget.entries.first()
        val message = "You've spent €${String.format("%.0f", category.value)} on ${category.key} (€${String.format("%.0f", limit)} budget)"

        val notification = NotificationCompat.Builder(applicationContext, "budget_alerts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Budget Alert!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun sendTestNotification(transactionCount: Int, spending: Map<String, Double>) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_alerts",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val message = if (spending.isEmpty()) {
            "No expenses today! Budget check working."
        } else {
            val topSpending = spending.maxByOrNull { it.value }
            "Budget check working! Today: ${topSpending?.key} €${String.format("%.0f", topSpending?.value)}"
        }

        val notification = NotificationCompat.Builder(applicationContext, "budget_alerts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Budget Check Test")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}
