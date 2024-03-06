import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.paycurrencyexchange.data.remote.ApiResponse
import com.example.paycurrencyexchange.data.repository.ExchangeRateRepository
import com.example.paycurrencyexchange.viewmodel.CurrencyExchangeViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class CurrencyExchangeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var exchangeRateRepository: FakeRepository

    private lateinit var viewModel: CurrencyExchangeViewModel

    @Before
    fun setUp() {
        exchangeRateRepository = FakeRepository()
        viewModel = CurrencyExchangeViewModel(exchangeRateRepository)
    }

    @Test
    fun fetchExchangeRates_on_init_updates_currencyList_and_calculatedExchangeRates() = runBlocking {
        val currencyListObserved = viewModel.currencyList.getOrAwaitValue()
        val calculatedRatesObserved = viewModel.calculatedExchangeRates.getOrAwaitValue()
        assertEquals(listOf("USD", "EUR"), currencyListObserved)
        assertEquals(2, calculatedRatesObserved.size) // More detailed assertions can be added
    }

    @Test
    fun changeCurrentCountry_updates_currentCurrency_and_recalculates_exchange_rates_() = runBlocking {
        viewModel.changeCurrentCountry("EUR", 100.0)

        // Verify current currency is updated
        assertEquals("EUR", viewModel.currentCurrency.getOrAwaitValue())

        // Verify recalculated exchange rates
        val calculatedRatesObserved = viewModel.calculatedExchangeRates.getOrAwaitValue()
        assertEquals(2, calculatedRatesObserved.size) // More detailed assertions can be added
    }
}

class FakeRepository : ExchangeRateRepository {
    override fun getExchangeRate(forceRefresh: Boolean): Flow<ApiResponse<Map<String, Double>>> {
        return flowOf(ApiResponse.success(mapOf("USD" to 1.0, "EUR" to 0.9)))
    }
}

/**
 * Observes a [LiveData] until it emits a value (or times out), and then returns the value.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

