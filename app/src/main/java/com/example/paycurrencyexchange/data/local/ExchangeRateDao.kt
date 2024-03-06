package com.example.paycurrencyexchange.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paycurrencyexchange.data.model.ExchangeRateItemEntity

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates_entity")
    suspend fun getExchangeRateList(): List<ExchangeRateItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRateList(exchangeRateItem: List<ExchangeRateItemEntity>)

}