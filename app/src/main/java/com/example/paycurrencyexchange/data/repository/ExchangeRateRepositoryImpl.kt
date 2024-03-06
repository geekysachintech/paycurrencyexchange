package com.example.paycurrencyexchange.data.repository

import com.example.paycurrencyexchange.data.local.ExchangeRateDao
import com.example.paycurrencyexchange.data.model.ExchangeRateItemEntity
import com.example.paycurrencyexchange.data.remote.ApiService
import com.example.paycurrencyexchange.utils.RateLimiter
import com.example.paycurrencyexchange.data.remote.ApiResponse
import com.example.paycurrencyexchange.utils.AppConstants.NETWORK_REQUEST_FAILED
import com.example.paycurrencyexchange.utils.AppConstants.NO_DATA_RECEIVED_FROM_NETWORK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    private val apiService: ApiService
) : ExchangeRateRepository {

    // Rate limiter to control the frequency of API requests.
    private val rateLimiter = RateLimiter(30, TimeUnit.MINUTES)

    private suspend fun fetchLocalData() = exchangeRateDao.getExchangeRateList()

    private suspend fun fetchRealTimeData() = apiService.getCurrencyExchangeRateData()

    //get required exchange rate data with flow to emit data asynchronously.
    override fun getExchangeRate(forceRefresh: Boolean) : Flow<ApiResponse<Map<String, Double>>> = flow {
        emit(ApiResponse.loading(null))

        val localData = fetchLocalData()
        val lastCachedTime = fetchLocalData().firstOrNull()?.timestamp

        // Check if it's necessary to fetch data from the network.
        if (rateLimiter.shouldFetch(lastCachedTime) || localData.isEmpty()){
            try {
                val response = fetchRealTimeData()

                if (response.isSuccessful){
                    response.body()?.rates?.let { rates->
                        val exchangeRate = rates.map { ExchangeRateItemEntity(currency = it.key, exchangeRate = it.value, timestamp = System.currentTimeMillis())  }
                        exchangeRateDao.insertExchangeRateList(exchangeRate)
                        emit(ApiResponse.success(rates))

                    }  ?: emit(ApiResponse.error(NO_DATA_RECEIVED_FROM_NETWORK, null))
                } else {
                    emit(ApiResponse.error(NETWORK_REQUEST_FAILED, null))
                }

            } catch (e: Exception){
                emit(ApiResponse.error("Failed to fetch data: ${e.message}", null))
            }
        } else {
            //fetching from the network is not necessary, emit the local data as a success state.
            val data = localData.associate { it.currency to it.exchangeRate }
            emit(ApiResponse.success(data))
        }
    }.catch { error ->
        emit(ApiResponse.error("An error occurred: ${error.message}", null))
    }.flowOn(Dispatchers.IO)

}