package com.example.pagingexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/*
 * This is for adapters that are only to inflate views without any item associated to it.
 * I don't think this is a pretty common thing but I guess it can be useful when required.
 */
private class SimpleOnlyViewRecyclerViewAdapter<VB : ViewBinding>(
	private inline val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
	private val count: Int = 1,
	private inline val onBind: RecyclerView.Adapter<out RecyclerView.ViewHolder>.(
		binding: VB,
		position: Int,
	) -> Unit,
) : RecyclerView.Adapter<SimpleOnlyViewRecyclerViewAdapter<VB>.ViewHolder>() {

	inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		binding = inflate(LayoutInflater.from(parent.context), parent, false)
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBind(holder.binding, position)

	override fun getItemCount() = count
}


@Suppress("FunctionName")
fun <VB : ViewBinding> SimpleRecyclerViewAdapter(
	inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
	itemCount: Int = 1,
	onBind: RecyclerView.Adapter<out RecyclerView.ViewHolder>.(
		binding: VB,
		position: Int,
	) -> Unit = { _, _ -> },
): RecyclerView.Adapter<out RecyclerView.ViewHolder> = SimpleOnlyViewRecyclerViewAdapter(
	inflate = inflate,
	count = itemCount,
	onBind = { binding, position ->

		onBind(binding, position)
	}
)