package com.example.paycurrencyexchange.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.paycurrencyexchange.data.local.ExchangeRateDao
import com.example.paycurrencyexchange.data.local.ExchangeRateDataBase
import com.example.paycurrencyexchange.data.model.ExchangeRateItemEntity
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeRateDaoTest {

    private lateinit var database: ExchangeRateDataBase
    private lateinit var dao: ExchangeRateDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ExchangeRateDataBase::class.java).build()
        dao = database.exchangeRateDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetExchangeRate() = runBlocking {
        val exchangeRate = ExchangeRateItemEntity(currency = "USD", exchangeRate = 1.0, timestamp = System.currentTimeMillis())
        dao.insertExchangeRateList(listOf(exchangeRate))

        val allExchangeRates = dao.getExchangeRateList()
        assertTrue(allExchangeRates.contains(exchangeRate))
    }
}
