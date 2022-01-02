package com.kotlinbyte.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlinbyte.example.databinding.CurrencyItemBinding
import kotlin.properties.Delegates

class CurrencyListAdapter : RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder>() {

    internal var collection: List<CurrencyDtoItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(currency: CurrencyDtoItem) = with(CurrencyItemBinding.bind(itemView)) {
            name.text = currency.symbol
            price.text = currency.price
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = CurrencyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun getItemCount(): Int = collection.size
}