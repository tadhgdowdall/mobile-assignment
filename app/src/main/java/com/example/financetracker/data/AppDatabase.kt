package com.example.financetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financetracker.Transaction

/**
 * AppDatabase - Main Room database class
 *
 * @Database annotation defines:
 * - entities: List of all tables in the database
 * - version: Database version number (increment when schema changes)
 * - exportSchema: Whether to export schema to a folder (false for simplicity)
 *
 * This is a singleton - only one instance exists in the app
 */
@Database(
    entities = [Transaction::class], // List all entity classes here
    version = 1, // Increment this when you change the schema
    exportSchema = false
)
@TypeConverters(Converters::class) // Register type converters for custom types
abstract class AppDatabase : RoomDatabase() {

    /**
     * Abstract function to get the DAO
     * Room will generate the implementation automatically
     */
    abstract fun transactionDao(): TransactionDao

    companion object {
        /**
         * @Volatile ensures the value is always up-to-date across threads
         * Prevents multiple instances in multi-threaded environment
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get the database instance (Singleton pattern)
         *
         * synchronized block ensures only one thread can execute this at a time
         * Double-check locking pattern for thread safety
         */
        fun getDatabase(context: Context): AppDatabase {
            // If instance already exists, return it
            return INSTANCE ?: synchronized(this) {
                // Double check - another thread might have created it
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_tracker_database" // Database file name
                )
                    // Migration strategy - for production, you'd add proper migrations
                    // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, etc.)
                    // For development, we can use fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration(dropAllTables = true) // Destroys and recreates DB on version change
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
