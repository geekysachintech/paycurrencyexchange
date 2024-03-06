package com.example.paycurrencyexchange.data.repository

import com.example.paycurrencyexchange.data.remote.ApiResponse
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {

    fun getExchangeRate(forceRefresh: Boolean) : Flow<ApiResponse<Map<String, Double>>>

}