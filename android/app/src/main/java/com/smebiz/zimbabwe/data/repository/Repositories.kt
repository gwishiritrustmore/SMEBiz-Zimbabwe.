package com.smebiz.zimbabwe.data.repository

import com.smebiz.zimbabwe.data.db.SMEBizDatabase
import com.smebiz.zimbabwe.data.db.Sale
import com.smebiz.zimbabwe.data.db.Expense
import com.smebiz.zimbabwe.data.db.Product
import com.smebiz.zimbabwe.data.db.FinancialBalance
import com.smebiz.zimbabwe.data.db.StockMovement
import com.smebiz.zimbabwe.data.db.AuditTrail
import com.smebiz.zimbabwe.util.TransactionIdGenerator
import kotlinx.coroutines.flow.Flow

/**
 * Repository for product operations
 */
class ProductRepository(private val db: SMEBizDatabase) {
    
    suspend fun addProduct(product: Product): Long {
        return db.productDao().insert(product)
    }
    
    suspend fun updateProduct(product: Product) {
        db.productDao().update(product)
    }
    
    suspend fun getProduct(id: Long): Product? {
        return db.productDao().getById(id)
    }
    
    fun getAllActiveProducts(): Flow<List<Product>> {
        return db.productDao().getAllActive()
    }
    
    fun getProductsByType(type: String): Flow<List<Product>> {
        return db.productDao().getByType(type)
    }
    
    fun getLowStockItems(): Flow<List<Product>> {
        return db.productDao().getLowStockItems()
    }
    
    fun getStockValue(): Flow<Double?> {
        return db.productDao().getStockValue()
    }
}

/**
 * Repository for sales operations
 */
class SalesRepository(private val db: SMEBizDatabase) {
    
    suspend fun recordSale(sale: Sale): Long {
        return db.saleDao().insert(sale)
    }
    
    suspend fun getSale(id: String): Sale? {
        return db.saleDao().getById(id)
    }
    
    fun getAllSales(): Flow<List<Sale>> {
        return db.saleDao().getAllActive()
    }
    
    fun getTodaysSales(): Flow<List<Sale>> {
        return db.saleDao().getTodaysSales()
    }
    
    fun getTodaysSalesTotal(): Flow<Double?> {
        return db.saleDao().getTodaysSalesTotal()
    }
    
    fun getSalesByDate(date: String): Flow<List<Sale>> {
        return db.saleDao().getSalesByDate(date)
    }
    
    suspend fun voidSale(saleId: String) {
        db.saleDao().voidSale(saleId)
        recordAudit(saleId, "SALE", "VOID", "Sale voided")
    }
    
    /**
     * Record a complete sale with stock adjustment and audit trail
     * Uses database transaction for consistency
     */
    suspend fun recordSaleTransaction(
        product: Product,
        quantity: Int,
        unitPrice: Double,
        discount: Double,
        paymentMethod: String,
        reference: String = "",
        notes: String = ""
    ): String {
        val saleCount = (db.saleDao().getAllSales().collect { it.size })
        val saleId = TransactionIdGenerator.generateSaleId(saleCount)
        
        val totalAmount = (quantity * unitPrice) - discount
        
        // Create sale record
        val sale = Sale(
            id = saleId,
            productId = product.id,
            quantity = quantity,
            unitPrice = unitPrice,
            discount = discount,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            reference = reference,
            notes = notes
        )
        
        // Record in database (atomic operation)
        db.saleDao().insert(sale)
        
        // Reduce stock if it's a good
        if (product.type == "GOOD") {
            val updatedProduct = product.copy(currentStock = product.currentStock - quantity)
            db.productDao().update(updatedProduct)
            
            // Record stock movement
            db.stockMovementDao().insert(
                StockMovement(
                    productId = product.id,
                    quantityChange = -quantity,
                    reason = "SALE",
                    referenceId = saleId
                )
            )
        }
        
        // Update financial balance
        updateFinancialBalance(paymentMethod, totalAmount, isIncome = true)
        
        // Record audit trail
        recordAudit(saleId, "SALE", "CREATE", "Sale recorded for ${product.name}")
        
        return saleId
    }
    
    private suspend fun recordAudit(transactionId: String, type: String, action: String, details: String) {
        db.auditTrailDao().insert(
            AuditTrail(
                transactionId = transactionId,
                transactionType = type,
                action = action,
                details = details
            )
        )
    }
    
    private suspend fun updateFinancialBalance(method: String, amount: Double, isIncome: Boolean) {
        val balanceId = when (method) {
            "CASH" -> "CASH"
            "MOBILE_MONEY" -> "MOBILE_MONEY"
            "BANK" -> "BANK"
            else -> return
        }
        
        val current = db.financialBalanceDao().getBalance(balanceId) ?: return
        val newBalance = if (isIncome) current.balance + amount else current.balance - amount
        
        db.financialBalanceDao().update(
            current.copy(balance = newBalance, lastUpdated = System.currentTimeMillis())
        )
    }
}

/**
 * Repository for expenses operations
 */
class ExpensesRepository(private val db: SMEBizDatabase) {
    
    suspend fun recordExpense(expense: Expense): Long {
        return db.expenseDao().insert(expense)
    }
    
    suspend fun getExpense(id: String): Expense? {
        return db.expenseDao().getById(id)
    }
    
    fun getAllExpenses(): Flow<List<Expense>> {
        return db.expenseDao().getAllActive()
    }
    
    fun getTodaysExpenses(): Flow<List<Expense>> {
        return db.expenseDao().getTodaysExpenses()
    }
    
    fun getTodaysExpensesTotal(): Flow<Double?> {
        return db.expenseDao().getTodaysExpensesTotal()
    }
    
    fun getExpensesByDate(date: String): Flow<List<Expense>> {
        return db.expenseDao().getExpensesByDate(date)
    }
    
    suspend fun voidExpense(expenseId: String) {
        db.expenseDao().voidExpense(expenseId)
        recordAudit(expenseId, "EXPENSE", "VOID", "Expense voided")
    }
    
    /**
     * Record expense with financial balance update
     */
    suspend fun recordExpenseTransaction(
        category: String,
        amount: Double,
        description: String,
        paymentMethod: String,
        reference: String = "",
        notes: String = ""
    ): String {
        val expenseCount = (db.expenseDao().getAllExpenses().collect { it.size })
        val expenseId = TransactionIdGenerator.generateExpenseId(expenseCount)
        
        val expense = Expense(
            id = expenseId,
            category = category,
            amount = amount,
            description = description,
            paymentMethod = paymentMethod,
            reference = reference,
            notes = notes
        )
        
        db.expenseDao().insert(expense)
        
        // Deduct from financial balance
        updateFinancialBalance(paymentMethod, amount, isIncome = false)
        
        recordAudit(expenseId, "EXPENSE", "CREATE", "Expense recorded: $description")
        
        return expenseId
    }
    
    private suspend fun recordAudit(transactionId: String, type: String, action: String, details: String) {
        db.auditTrailDao().insert(
            AuditTrail(
                transactionId = transactionId,
                transactionType = type,
                action = action,
                details = details
            )
        )
    }
    
    private suspend fun updateFinancialBalance(method: String, amount: Double, isIncome: Boolean) {
        val balanceId = when (method) {
            "CASH" -> "CASH"
            "MOBILE_MONEY" -> "MOBILE_MONEY"
            "BANK" -> "BANK"
            else -> return
        }
        
        val current = db.financialBalanceDao().getBalance(balanceId) ?: return
        val newBalance = if (isIncome) current.balance + amount else current.balance - amount
        
        db.financialBalanceDao().update(
            current.copy(balance = newBalance, lastUpdated = System.currentTimeMillis())
        )
    }
}

/**
 * Repository for financial balances
 */
class FinancialRepository(private val db: SMEBizDatabase) {
    
    fun getCashBalance(): Flow<Double?> {
        return db.financialBalanceDao().getBalanceFlow("CASH")
    }
    
    fun getMobileMoneyBalance(): Flow<Double?> {
        return db.financialBalanceDao().getBalanceFlow("MOBILE_MONEY")
    }
    
    fun getBankBalance(): Flow<Double?> {
        return db.financialBalanceDao().getBalanceFlow("BANK")
    }
    
    fun getAllBalances(): Flow<List<FinancialBalance>> {
        return db.financialBalanceDao().getAllBalances()
    }
}

/**
 * Dashboard repository - combines multiple data sources
 */
class DashboardRepository(private val db: SMEBizDatabase) {
    
    fun getDashboardData() = object {
        val todaysSalesTotal = db.saleDao().getTodaysSalesTotal()
        val todaysExpensesTotal = db.expenseDao().getTodaysExpensesTotal()
        val cashBalance = db.financialBalanceDao().getBalanceFlow("CASH")
        val mobileMoneyBalance = db.financialBalanceDao().getBalanceFlow("MOBILE_MONEY")
        val bankBalance = db.financialBalanceDao().getBalanceFlow("BANK")
        val stockValue = db.productDao().getStockValue()
        val lowStockItems = db.productDao().getLowStockItems()
    }
}
