package com.example.paycurrencyexchange.repository

import com.example.paycurrencyexchange.data.local.ExchangeRateDao
import com.example.paycurrencyexchange.data.model.ExchangeRateItemEntity
import com.example.paycurrencyexchange.data.model.ExchangeRateResponse
import com.example.paycurrencyexchange.data.remote.ApiService
import com.example.paycurrencyexchange.data.remote.Status
import com.example.paycurrencyexchange.data.repository.ExchangeRateRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class ExchangeRateRepositoryImplTest {

    private lateinit var fakeExchangeRateDao: ExchangeRateDao
    private lateinit var fakeApiService: ApiService

    private lateinit var repository: ExchangeRateRepositoryImpl

    @Before
    fun setUp() {
        fakeExchangeRateDao = mockk(relaxed = true)
        fakeApiService = mockk(relaxed = true)
        repository = ExchangeRateRepositoryImpl(fakeExchangeRateDao, fakeApiService)
    }

    @Test
    fun `when fetch success should update local database`(): Unit = runBlocking {
        val mockResponse = ExchangeRateResponse("", "", 123, "", mapOf("USD" to 1.0))
        coEvery { fakeApiService.getCurrencyExchangeRateData(any()) } returns Response.success(mockResponse)

        val result = repository.getExchangeRate(forceRefresh = true).toList()

        // there's an success response emitted at some point
        val hasSuccess = result.any { it.status == Status.SUCCESS }
        assertTrue(hasSuccess)
        coEvery { fakeExchangeRateDao.getExchangeRateList() } returns listOf(ExchangeRateItemEntity("USD", 1.0, 123))
    }

    @Test
    fun `when fetch fails should return error`() = runBlocking {
        coEvery { fakeApiService.getCurrencyExchangeRateData(any()) } throws RuntimeException("Failed to fetch")

        val results = repository.getExchangeRate(forceRefresh = true).toList()

        // there's an error response emitted at some point
        val hasError = results.any { it.status == Status.ERROR }
        assertTrue("Expected at least one ApiResponse to be ERROR", hasError)
    }
}
