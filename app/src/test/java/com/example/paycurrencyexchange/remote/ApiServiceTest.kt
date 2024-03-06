package com.example.paycurrencyexchange.remote

import com.example.paycurrencyexchange.data.remote.ApiService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var service: ApiService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        // Start the server.
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        // Shut down the server.
        mockWebServer.shutdown()
    }

    @Test
    fun `getCurrencyExchangeRateData returns data`() = runBlocking {
        // Define a mock response.
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"base":"USD","rates":{"EUR":0.9}}""")
        mockWebServer.enqueue(mockResponse)

        // Make the API call.
        val response = service.getCurrencyExchangeRateData()

        // Assert the response is not null.
        assertNotNull(response.body())
        assertNotNull(response.body()?.rates)
        assert(response.body()?.rates?.containsKey("EUR") == true)
    }

    @Test
    fun `getCurrency ExchangeRateData returns Error`() = runBlocking {
        // Define a mock error response.
        val mockResponse = MockResponse()
            .setResponseCode(404) // Not Found
            .setBody("""{"error":"currency not found"}""")
        mockWebServer.enqueue(mockResponse)

        // Make the API call.
        val response = service.getCurrencyExchangeRateData()

        // Assert the response is as expected for an error.
        assertNotNull(response)
        assert(!response.isSuccessful) // Verify that the response is marked as unsuccessful
        assertEquals(response.code(), 404) // Verify the correct error code is received
    }

    @Test
    fun `getCurrency ExchangeRateData wrongObjectStructure`() = runBlocking {
        // Define a mock response with an unexpected object structure.
        val mockResponse = MockResponse()
            .setResponseCode(200) // HTTP OK
            .setBody("""{"unexpectedKey":"unexpectedValue"}""") // This does not match the expected ExchangeRateResponse structure
        mockWebServer.enqueue(mockResponse)

        // Make the API call.
        val response = service.getCurrencyExchangeRateData()

        // Since Retrofit does not throw an exception for unknown fields, you would typically check if the expected fields are missing.
        // Assuming your ExchangeRateResponse has a non-nullable 'rates' field or a similar mandatory structure, you can assert on its absence or nullity.
        val body = response.body()
        assertNotNull(body)
        assertNull(body?.rates)
        assertTrue(body?.rates?.isEmpty() ?: true)
    }


}
