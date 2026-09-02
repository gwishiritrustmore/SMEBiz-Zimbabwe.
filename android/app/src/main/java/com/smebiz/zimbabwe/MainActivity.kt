package com.smebiz.zimbabwe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smebiz.zimbabwe.databinding.ActivityMainBinding
import com.smebiz.zimbabwe.ui.dashboard.DashboardActivity
import com.smebiz.zimbabwe.ui.products.ProductsActivity
import com.smebiz.zimbabwe.ui.sales.SalesActivity
import com.smebiz.zimbabwe.ui.expenses.ExpensesActivity
import com.smebiz.zimbabwe.ui.settings.SettingsActivity

/**
 * Main launcher activity - Navigation hub
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.dashboardBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        
        binding.productsBtn.setOnClickListener {
            startActivity(Intent(this, ProductsActivity::class.java))
        }
        
        binding.salesBtn.setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }
        
        binding.expensesBtn.setOnClickListener {
            startActivity(Intent(this, ExpensesActivity::class.java))
        }
        
        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
