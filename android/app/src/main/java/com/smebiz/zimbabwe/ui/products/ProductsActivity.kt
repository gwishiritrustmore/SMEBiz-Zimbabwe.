package com.smebiz.zimbabwe.ui.products

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smebiz.zimbabwe.R
import com.smebiz.zimbabwe.databinding.ActivityProductsBinding

/**
 * Products/Services Management Activity
 */
class ProductsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProductsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.products_services)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
