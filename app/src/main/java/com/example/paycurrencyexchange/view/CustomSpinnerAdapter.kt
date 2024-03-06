package com.example.paycurrencyexchange.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.paycurrencyexchange.databinding.CustomSpinnerItemBinding // Import your generated binding class

class CustomSpinnerAdapter(context: Context, items: List<String>) :
    ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.tag as? CustomSpinnerItemBinding ?: createBinding(parent)
        binding.customSpinnerItemText.text = getItem(position) ?: ""
        return binding.root.apply { tag = binding }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.tag as? CustomSpinnerItemBinding ?: createBinding(parent)
        binding.customSpinnerItemText.text = getItem(position) ?: ""
        return binding.root.apply { tag = binding }
    }

    private fun createBinding(parent: ViewGroup): CustomSpinnerItemBinding {
        return CustomSpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
    }
}
