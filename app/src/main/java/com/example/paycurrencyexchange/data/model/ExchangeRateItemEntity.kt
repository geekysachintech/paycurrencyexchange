package com.example.paycurrencyexchange.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates_entity")
data class ExchangeRateItemEntity (

    @PrimaryKey
    @ColumnInfo("currency")
    val currency: String,

    @ColumnInfo("exchange_rates")
    val exchangeRate: Double,

    @ColumnInfo("timestamp")
    val timestamp: Long

)