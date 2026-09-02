package com.smebiz.zimbabwe.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Product/Service entity for inventory and pricing
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val code: String, // SKU or service code
    val category: String,
    val type: String, // "GOOD" or "SERVICE"
    
    // Pricing (do not overwrite historical prices)
    val buyingPrice: Double,
    val sellingPrice: Double,
    
    val unit: String, // "Unit", "KG", "Litre", etc.
    val openingStock: Int = 0, // Only for GOOD type
    val currentStock: Int = 0, // Only for GOOD type
    val reorderLevel: Int = 0,
    
    val isActive: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Price history for audit trail
 */
@Entity(tableName = "price_history")
data class PriceHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val productId: Long,
    val buyingPrice: Double,
    val sellingPrice: Double,
    
    val changedAt: Long = System.currentTimeMillis()
)

/**
 * Sale transaction entity
 */
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey
    val id: String, // Format: SALE-20260902-000001
    
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    
    // Payment method
    val paymentMethod: String, // "CASH", "MOBILE_MONEY", "BANK", "CREDIT"
    
    // Transaction reference
    val reference: String = "",
    
    val notes: String = "",
    val isVoided: Boolean = false,
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Expense transaction entity
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id: String, // Format: EXP-20260902-000001
    
    val category: String,
    val amount: Double,
    val description: String,
    
    // Payment method
    val paymentMethod: String, // "CASH", "MOBILE_MONEY", "BANK"
    
    // Transaction reference
    val reference: String = "",
    
    val notes: String = "",
    val isVoided: Boolean = false,
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Financial balance tracker
 */
@Entity(tableName = "financial_balances")
data class FinancialBalance(
    @PrimaryKey
    val id: String, // "CASH", "MOBILE_MONEY", "BANK"
    
    val balance: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Customer/Debtor entity for future use
 */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    
    val totalOwing: Double = 0.0,
    val isActive: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Supplier/Creditor entity for future use
 */
@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    
    val totalOwed: Double = 0.0,
    val isActive: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Stock history for audit trail
 */
@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val productId: Long,
    val quantityChange: Int,
    val reason: String, // "SALE", "PURCHASE", "ADJUSTMENT", "RETURN"
    val referenceId: String = "", // Sale ID, Purchase ID, etc.
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Audit trail for financial transactions
 */
@Entity(tableName = "audit_trail")
data class AuditTrail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val transactionId: String,
    val transactionType: String, // "SALE", "EXPENSE", "STOCK_ADJUSTMENT"
    val action: String, // "CREATE", "UPDATE", "VOID", "REVERSE"
    val details: String = "",
    
    val createdAt: Long = System.currentTimeMillis()
)
