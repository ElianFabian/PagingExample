package com.example.pagingexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingexample.databinding.ItemRetryBinding

class SimpleLoadStateAdapter(
	private val onRetry: () -> Unit,
) : LoadStateAdapter<SimpleLoadStateAdapter.ViewHolder>() {
	class ViewHolder(val binding: ItemRetryBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		val binding = ItemRetryBinding.inflate(inflater, parent, false)

		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {

		val isLoading = loadState is LoadState.Loading
		val isError = loadState is LoadState.Error

		holder.binding.apply {
			pbIsLoading.isVisible = isLoading
			btnRetry.apply {
				isVisible = isError
				setOnClickListener {
					onRetry()
				}
			}
		}
	}
}