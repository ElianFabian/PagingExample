package com.example.pagingexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SingleItemLoadStateAdapter<VB : ViewBinding>(
	private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
) : LoadStateAdapter<SingleItemLoadStateAdapter<VB>.ViewHolder>() {

	abstract fun onBind(binding: VB, loadState: LoadState, holder: ViewHolder)


	final override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		val binding = inflate(inflater, parent, false)

		return ViewHolder(binding)
	}

	final override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {

		onBind(holder.binding as VB, loadState, holder)
	}

	inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}