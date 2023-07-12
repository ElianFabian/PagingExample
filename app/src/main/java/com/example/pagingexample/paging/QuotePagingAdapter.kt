package com.example.pagingexample.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingexample.databinding.ItemQuoteBinding
import com.example.pagingexample.models.QuoteModel

class QuotePagingAdapter : PagingDataAdapter<QuoteModel, QuotePagingAdapter.ViewHolder>(
	object : DiffUtil.ItemCallback<QuoteModel>() {
		override fun areItemsTheSame(oldItem: QuoteModel, newItem: QuoteModel) = oldItem.id == newItem.id

		override fun areContentsTheSame(oldItem: QuoteModel, newItem: QuoteModel) = oldItem == newItem
	}
) {
	inner class ViewHolder(val binding: ItemQuoteBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemQuoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)

		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)

		item?.also {
			holder.binding.tvQuote.text = "$position --- ${it.content}"
		}
	}
}