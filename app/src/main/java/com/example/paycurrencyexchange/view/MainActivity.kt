package com.example.paycurrencyexchange.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paycurrencyexchange.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpFragmentBase()
    }

    private fun setUpFragmentBase(){
        val exampleFragment = CurrencyExchangeFragment.newInstance()
        supportFragmentManager.beginTransaction().apply {
            replace(binding.flBaseLayout.id, exampleFragment)
            commit()
        }
    }
}