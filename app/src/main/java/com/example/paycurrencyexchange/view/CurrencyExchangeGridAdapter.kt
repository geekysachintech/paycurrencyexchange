package com.example.paycurrencyexchange.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paycurrencyexchange.data.model.ExchangeRateItem
import com.example.paycurrencyexchange.databinding.CurrencyGridItemBinding

class CurrencyExchangeGridAdapter : RecyclerView.Adapter<CurrencyExchangeGridAdapter.ViewHolder>() {

    private var items: List<ExchangeRateItem> = emptyList()

    class ViewHolder(val binding: CurrencyGridItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CurrencyGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = items[position]
        with(holder.binding){
            tvCurrencyValue.text = current.value.toString()
            tvCurrencyName.text = current.currency
        }
    }

    fun submitList(newItems: List<ExchangeRateItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}