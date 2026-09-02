package com.smebiz.zimbabwe.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.smebiz.zimbabwe.R
import com.smebiz.zimbabwe.data.db.SMEBizDatabase
import com.smebiz.zimbabwe.data.repository.DashboardRepository
import com.smebiz.zimbabwe.databinding.ActivityDashboardBinding
import com.smebiz.zimbabwe.util.CurrencyUtils
import com.smebiz.zimbabwe.util.DateTimeUtils
import kotlinx.coroutines.launch

/**
 * Dashboard Activity - displays business overview
 */
class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: SMEBizDatabase
    private lateinit var repository: DashboardRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.dashboard)
        
        db = SMEBizDatabase.getDatabase(this)
        repository = DashboardRepository(db)
        
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        lifecycleScope.launch {
            val dashData = repository.getDashboardData()
            
            // Observe today's sales
            dashData.todaysSalesTotal.collect { total ->
                binding.todaysSalesAmount.text = CurrencyUtils.formatCurrency(total ?: 0.0)
            }
            
            // Observe today's expenses
            dashData.todaysExpensesTotal.collect { total ->
                binding.todaysExpensesAmount.text = CurrencyUtils.formatCurrency(total ?: 0.0)
            }
            
            // Observe balances
            dashData.cashBalance.collect { balance ->
                binding.cashBalanceAmount.text = CurrencyUtils.formatCurrency(balance ?: 0.0)
            }
            
            dashData.mobileMoneyBalance.collect { balance ->
                binding.mobileMoneyBalanceAmount.text = CurrencyUtils.formatCurrency(balance ?: 0.0)
            }
            
            dashData.bankBalance.collect { balance ->
                binding.bankBalanceAmount.text = CurrencyUtils.formatCurrency(balance ?: 0.0)
            }
            
            // Stock value
            dashData.stockValue.collect { value ->
                binding.stockValueAmount.text = CurrencyUtils.formatCurrency(value ?: 0.0)
            }
            
            // Low stock alerts
            dashData.lowStockItems.collect { items ->
                binding.lowStockCount.text = items.size.toString()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
