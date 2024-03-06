package com.example.paycurrencyexchange.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.paycurrencyexchange.data.remote.Status
import com.example.paycurrencyexchange.databinding.FragmentCurrencyExchangeBinding
import com.example.paycurrencyexchange.utils.AppConstants.DEFAULT_BASE_CURRENCY
import com.example.paycurrencyexchange.utils.AppConstants.SOMETHING_WENT_WRONG
import com.example.paycurrencyexchange.utils.gone
import com.example.paycurrencyexchange.utils.showToast
import com.example.paycurrencyexchange.viewmodel.CurrencyExchangeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyExchangeFragment : Fragment() {

    private var _binding: FragmentCurrencyExchangeBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: CurrencyExchangeViewModel by viewModels()
    private var customSpinnerAdapter : CustomSpinnerAdapter?= null
    private val currencyExchangeGridAdapter by lazy { CurrencyExchangeGridAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyExchangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpSpinnerAdapter()
        afterInputValueChanged()
        setUpGridLayout()
    }

    /**
     * set up target currency selection adapter
     */
    private fun setUpSpinnerAdapter(){
        binding.switchCountrySpinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    changeCurrentTarget(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    /**
     * change target currency value & also fetch current currency outcome
     */
    private fun changeCurrentTarget(position: Int){
        val currency = viewModel.findTargetCurrencyFromPosition(position) ?: DEFAULT_BASE_CURRENCY
        if (viewModel.currentCurrency.value == currency)
            return
        viewModel.changeCurrentCountry(currency, getInputValue())
    }

    /**
     * fetch user input from editText
     */
    private fun getInputValue() : Double {
        var inputValue = 1.0
        if (binding.etCurrencyInput.text.toString().isNotEmpty()) {
            inputValue = binding.etCurrencyInput.text.toString().toDouble()
        }
        return inputValue
    }

    /**
     * when user completed entering the input
     */
    private fun afterInputValueChanged(){
        binding.etCurrencyInput.doAfterTextChanged { input ->
            viewModel.calculateExchangeRates(
                amount = getInputValue(),
                baseCurrency = viewModel.currentCurrency.value ?: DEFAULT_BASE_CURRENCY,
            )
        }
    }

    /**
     * observe observers from viewModel
     */
    private fun setUpObservers() {
        viewModel.exchangeRates.observe(viewLifecycleOwner) { response ->
            when(response.status){
                Status.SUCCESS -> binding.progressBar.visibility = View.GONE
                Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
                Status.ERROR -> {
                    binding.progressBar.gone()
                    requireContext().showToast(response.message ?: SOMETHING_WENT_WRONG)
                }
            }
        }

        viewModel.currencyList.observe(viewLifecycleOwner) { currencyNames ->
            currencyNames?.takeIf { it.isNotEmpty() }?.let {
                customSpinnerAdapter = CustomSpinnerAdapter(requireContext(), it)
                binding.switchCountrySpinner.apply {
                    adapter = customSpinnerAdapter
                    setSelection(viewModel.findPositionFromCurrency(it), true)
                }
            } ?: Log.d("setUpObservers", "currencyNames is null or empty")
        }

        viewModel.calculatedExchangeRates.observe(viewLifecycleOwner) { exchangeRateItems ->
            exchangeRateItems?.let {
                currencyExchangeGridAdapter.submitList(it)
            } ?: Log.d("setUpObservers", "exchangeRateItems is null")
        }
    }

    /**
     * setup currency grid using recyclerview
     */
    private fun setUpGridLayout(){
        with(binding.rvCurrencyExchange) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = currencyExchangeGridAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CurrencyExchangeFragment()
    }
}