package com.smebiz.zimbabwe.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Database for SMEBiz Zimbabwe
 * Maintains offline-first local storage with clean architecture for future features
 */
@Database(
    entities = [
        Product::class,
        PriceHistory::class,
        Sale::class,
        Expense::class,
        FinancialBalance::class,
        Customer::class,
        Supplier::class,
        StockMovement::class,
        AuditTrail::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SMEBizDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun saleDao(): SaleDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun financialBalanceDao(): FinancialBalanceDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun auditTrailDao(): AuditTrailDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    
    companion object {
        @Volatile
        private var INSTANCE: SMEBizDatabase? = null
        
        fun getDatabase(context: Context): SMEBizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SMEBizDatabase::class.java,
                    "smebiz_zimbabwe.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Database callback for initialization
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Initialize default financial balances
            db.execSQL("INSERT INTO financial_balances (id, balance, lastUpdated) VALUES ('CASH', 0.0, ${System.currentTimeMillis()})")
            db.execSQL("INSERT INTO financial_balances (id, balance, lastUpdated) VALUES ('MOBILE_MONEY', 0.0, ${System.currentTimeMillis()})")
            db.execSQL("INSERT INTO financial_balances (id, balance, lastUpdated) VALUES ('BANK', 0.0, ${System.currentTimeMillis()})")
        }
    }
}
