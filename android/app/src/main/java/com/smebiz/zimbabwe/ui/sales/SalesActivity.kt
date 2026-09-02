package com.smebiz.zimbabwe.ui.sales

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smebiz.zimbabwe.R
import com.smebiz.zimbabwe.databinding.ActivitySalesBinding

/**
 * Sales Recording Activity
 */
class SalesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySalesBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.sales)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
