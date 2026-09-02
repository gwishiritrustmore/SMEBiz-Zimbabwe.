package com.smebiz.zimbabwe.ui.expenses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smebiz.zimbabwe.R
import com.smebiz.zimbabwe.databinding.ActivityExpensesBinding

/**
 * Expenses Recording Activity
 */
class ExpensesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityExpensesBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expenses)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.expenses)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
