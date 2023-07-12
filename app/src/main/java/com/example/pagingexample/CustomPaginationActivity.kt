package com.example.pagingexample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.pagingexample.databinding.LayoutPaginatedListBinding
import com.example.pagingexample.paging.QuoteListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomPaginationActivity : AppCompatActivity() {
	private val viewModel by viewModels<CustomQuoteViewModel>()
	private lateinit var binding: LayoutPaginatedListBinding

	private val quotePagingAdapter = QuoteListAdapter()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = LayoutPaginatedListBinding.inflate(layoutInflater)

		setContentView(binding.root)

		subscribeToEvents()
		initUi()
	}

	private fun initUi() {
		binding.rvQuotes.adapter = quotePagingAdapter
		binding.rvQuotes.addOnScrollListener(object : InfiniteScrollListener(
			layoutManager = binding.rvQuotes.layoutManager as LinearLayoutManager,
		) {
			override fun onLoadMore() {
				viewModel.loadNextItems()
			}

			override fun isDataLoading(): Boolean {
				return viewModel.state.value.isLoading
			}
		})
	}

	private fun subscribeToEvents() {
		lifecycleScope.launch {
			viewModel.state.flowWithLifecycle(lifecycle)
				.map { it.items }
				.collectLatest { quotes ->
					quotePagingAdapter.submitList(quotes)
				}
		}
		lifecycleScope.launch { 
			viewModel.state.flowWithLifecycle(lifecycle)
				.collectLatest { state -> 
					binding.pbIsLoading.isVisible = state.isLoading
				}
		}
	}
}