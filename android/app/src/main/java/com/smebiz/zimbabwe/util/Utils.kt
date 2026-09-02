package com.smebiz.zimbabwe.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Transaction ID generator with human-readable format
 */
object TransactionIdGenerator {
    
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    private var lastSequence: MutableMap<String, Int> = mutableMapOf()
    
    /**
     * Generate transaction ID with format: TYPE-YYYYMMDD-XXXXXX
     * Example: SALE-20260902-000001
     */
    fun generateSaleId(existingSaleCount: Int): String {
        return generateId("SALE", existingSaleCount)
    }
    
    fun generateExpenseId(existingExpenseCount: Int): String {
        return generateId("EXP", existingExpenseCount)
    }
    
    fun generatePurchaseId(existingPurchaseCount: Int): String {
        return generateId("PUR", existingPurchaseCount)
    }
    
    private fun generateId(prefix: String, count: Int): String {
        val date = dateFormat.format(System.currentTimeMillis())
        val sequence = String.format("%06d", count + 1)
        return "$prefix-$date-$sequence"
    }
}

/**
 * Date/Time utilities
 */
object DateTimeUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun getCurrentDate(): String = LocalDate.now().format(dateFormatter)
    fun getCurrentDateTime(): String = LocalDateTime.now().format(dateTimeFormatter)
    fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }
    
    fun formatDateTime(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date)
    }
    
    fun formatTime(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        return SimpleDateFormat("HH:mm", Locale.US).format(date)
    }
}

/**
 * Currency formatting utilities
 */
object CurrencyUtils {
    
    fun formatCurrency(amount: Double): String {
        return String.format("ZWL %.2f", amount)
    }
    
    fun formatNumber(value: Int): String {
        return String.format("%,d", value)
    }
    
    fun formatDecimal(value: Double): String {
        return String.format("%.2f", value)
    }
}
