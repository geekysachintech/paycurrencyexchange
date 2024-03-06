package com.example.paycurrencyexchange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paycurrencyexchange.data.model.ExchangeRateItem
import com.example.paycurrencyexchange.data.repository.ExchangeRateRepository
import com.example.paycurrencyexchange.data.remote.ApiResponse
import com.example.paycurrencyexchange.data.remote.Status
import com.example.paycurrencyexchange.utils.AppConstants.DEFAULT_BASE_CURRENCY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository
) :  ViewModel() {

    //to hold the current selected currency
    private val _currentCurrency = MutableLiveData(DEFAULT_BASE_CURRENCY)
    val currentCurrency: LiveData<String> = _currentCurrency

    //to holds the latest UI state
    private val _exchangeRates = MutableLiveData<ApiResponse<Map<String, Double>>>()
    val exchangeRates: LiveData<ApiResponse<Map<String, Double>>> = _exchangeRates

    //holds a list of available currencies, derived from the exchange rates.
    private val _currencyList = MutableLiveData<List<String>>()
    val currencyList: LiveData<List<String>> = _currencyList

    //holds calculated exchange rates based on the user's input & selection.
    private val _calculatedExchangeRates = MutableLiveData<List<ExchangeRateItem>>()
    val calculatedExchangeRates: LiveData<List<ExchangeRateItem>> = _calculatedExchangeRates

    init {
        fetchExchangeRates()
    }

    //to change the current currency and recalculate exchange rates based on a new amount.
    fun changeCurrentCountry(string: String, amount: Double){
        _currentCurrency.postValue(string)
        calculateExchangeRates(amount, string)
    }

    //to fetch actual exchange rates or state from the repository
    private fun fetchExchangeRates(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            exchangeRateRepository.getExchangeRate(forceUpdate).collect { response ->
                _exchangeRates.value = response
                if (response.status == Status.SUCCESS) {
                    response.data?.let { rates ->
                        // Update the currency list when exchange rates are successfully fetched
                        _currencyList.postValue(rates.keys.toList())
                        // Perform an initial calculation of exchange rates
                        calculateExchangeRates(1.0, _currentCurrency.value!!)
                    }
                }
            }
        }
    }

    fun findPositionFromCurrency(list: List<String>, target: String = DEFAULT_BASE_CURRENCY) = list.indexOf(target)

    fun findTargetCurrencyFromPosition(itemPosition: Int) = currencyList.value?.get(itemPosition)

    //to calculate exchange rates for all currencies based on a given amount and base currency.
    fun calculateExchangeRates(amount: Double, baseCurrency: String) {
        val rates = _exchangeRates.value?.data ?: return
        val baseRate = rates[baseCurrency] ?: 1.0
        val calculatedRates = rates.map { (currency, rate) ->
            val value = (rate/baseRate) * amount
            val formattedValue = String.format("%.3f", value).toDouble()
            ExchangeRateItem(currency, formattedValue)
        }
        _calculatedExchangeRates.postValue(calculatedRates)
    }

}