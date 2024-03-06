package com.example.paycurrencyexchange.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paycurrencyexchange.data.model.ExchangeRateItemEntity

@Database(entities = [ExchangeRateItemEntity::class], version = 1)
abstract class ExchangeRateDataBase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao
}