package com.smebiz.zimbabwe.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Product DAO for database operations
 */
@Dao
interface ProductDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long
    
    @Update
    suspend fun update(product: Product)
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): Product?
    
    @Query("SELECT * FROM products WHERE code = :code")
    suspend fun getByCode(code: String): Product?
    
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Product>>
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    fun getByType(type: String): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE currentStock < reorderLevel AND type = 'GOOD' AND isActive = 1")
    fun getLowStockItems(): Flow<List<Product>>
    
    @Query("SELECT SUM(currentStock * buyingPrice) FROM products WHERE type = 'GOOD' AND isActive = 1")
    fun getStockValue(): Flow<Double?>
}

/**
 * Price History DAO
 */
@Dao
interface PriceHistoryDao {
    
    @Insert
    suspend fun insert(priceHistory: PriceHistory)
    
    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY changedAt DESC")
    fun getHistoryForProduct(productId: Long): Flow<List<PriceHistory>>
}

/**
 * Sale DAO for database operations
 */
@Dao
interface SaleDao {
    
    @Insert
    suspend fun insert(sale: Sale): Long
    
    @Update
    suspend fun update(sale: Sale)
    
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getById(id: String): Sale?
    
    @Query("SELECT * FROM sales WHERE isVoided = 0 ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE DATE(createdAt / 1000, 'unixepoch') = DATE('now')")
    fun getTodaysSales(): Flow<List<Sale>>
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE isVoided = 0 AND DATE(createdAt / 1000, 'unixepoch') = DATE('now')")
    fun getTodaysSalesTotal(): Flow<Double?>
    
    @Query("SELECT * FROM sales WHERE DATE(createdAt / 1000, 'unixepoch') = :date")
    fun getSalesByDate(date: String): Flow<List<Sale>>
    
    @Query("UPDATE sales SET isVoided = 1 WHERE id = :id")
    suspend fun voidSale(id: String)
}

/**
 * Expense DAO for database operations
 */
@Dao
interface ExpenseDao {
    
    @Insert
    suspend fun insert(expense: Expense): Long
    
    @Update
    suspend fun update(expense: Expense)
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: String): Expense?
    
    @Query("SELECT * FROM expenses WHERE isVoided = 0 ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE DATE(createdAt / 1000, 'unixepoch') = DATE('now')")
    fun getTodaysExpenses(): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE isVoided = 0 AND DATE(createdAt / 1000, 'unixepoch') = DATE('now')")
    fun getTodaysExpensesTotal(): Flow<Double?>
    
    @Query("SELECT * FROM expenses WHERE DATE(createdAt / 1000, 'unixepoch') = :date")
    fun getExpensesByDate(date: String): Flow<List<Expense>>
    
    @Query("UPDATE expenses SET isVoided = 1 WHERE id = :id")
    suspend fun voidExpense(id: String)
}

/**
 * Financial Balance DAO
 */
@Dao
interface FinancialBalanceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balance: FinancialBalance)
    
    @Update
    suspend fun update(balance: FinancialBalance)
    
    @Query("SELECT * FROM financial_balances WHERE id = :id")
    suspend fun getBalance(id: String): FinancialBalance?
    
    @Query("SELECT * FROM financial_balances")
    fun getAllBalances(): Flow<List<FinancialBalance>>
    
    @Query("SELECT balance FROM financial_balances WHERE id = :id")
    fun getBalanceFlow(id: String): Flow<Double?>
}

/**
 * Stock Movement DAO for audit trail
 */
@Dao
interface StockMovementDao {
    
    @Insert
    suspend fun insert(movement: StockMovement): Long
    
    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY createdAt DESC")
    fun getMovementsForProduct(productId: Long): Flow<List<StockMovement>>
    
    @Query("SELECT * FROM stock_movements WHERE referenceId = :referenceId")
    fun getMovementsForReference(referenceId: String): Flow<List<StockMovement>>
}

/**
 * Audit Trail DAO
 */
@Dao
interface AuditTrailDao {
    
    @Insert
    suspend fun insert(auditTrail: AuditTrail)
    
    @Query("SELECT * FROM audit_trail WHERE transactionId = :transactionId")
    fun getAuditForTransaction(transactionId: String): Flow<List<AuditTrail>>
    
    @Query("SELECT * FROM audit_trail ORDER BY createdAt DESC")
    fun getAllAuditTrail(): Flow<List<AuditTrail>>
}

/**
 * Customer DAO for future use
 */
@Dao
interface CustomerDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer): Long
    
    @Update
    suspend fun update(customer: Customer)
    
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: Long): Customer?
    
    @Query("SELECT * FROM customers WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Customer>>
}

/**
 * Supplier DAO for future use
 */
@Dao
interface SupplierDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier): Long
    
    @Update
    suspend fun update(supplier: Supplier)
    
    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: Long): Supplier?
    
    @Query("SELECT * FROM suppliers WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Supplier>>
}
