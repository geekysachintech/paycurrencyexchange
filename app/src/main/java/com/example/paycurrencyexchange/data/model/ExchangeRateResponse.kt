package com.example.paycurrencyexchange.data.model

data class ExchangeRateResponse (
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String?,
    val rates: Map<String, Double>?
)