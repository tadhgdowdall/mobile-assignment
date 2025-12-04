package com.example.financetracker.data

import androidx.room.TypeConverter
import com.example.financetracker.TransactionType

/**
 * Converters - Type converters for Room database
 *
 * Room doesn't know how to store custom types like enums
 * TypeConverters tell Room how to convert custom types to/from primitive types
 *
 * These converters convert TransactionType enum to String for storage
 */
class Converters {

    /**
     * Convert TransactionType enum to String for database storage
     *
     * @TypeConverter tells Room this converts a custom type to a storable type
     * @param type The enum value (INCOME or EXPENSE)
     * @return String representation ("INCOME" or "EXPENSE")
     */
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name // Returns "INCOME" or "EXPENSE"
    }

    /**
     * Convert String from database back to TransactionType enum
     *
     * @param value String from database ("INCOME" or "EXPENSE")
     * @return The corresponding enum value
     */
    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value) // Converts "INCOME" to TransactionType.INCOME
    }
}
