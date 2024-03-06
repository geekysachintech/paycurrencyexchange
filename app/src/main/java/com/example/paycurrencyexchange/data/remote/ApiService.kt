package com.example.paycurrencyexchange.data.remote

import com.example.paycurrencyexchange.BuildConfig
import com.example.paycurrencyexchange.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest.json")
    suspend fun getCurrencyExchangeRateData(@Query("app_id") appId: String = BuildConfig.API_KEY) : Response<ExchangeRateResponse>

}