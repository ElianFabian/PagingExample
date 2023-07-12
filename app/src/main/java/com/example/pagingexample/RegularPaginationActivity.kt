package com.example.pagingexample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.pagingexample.databinding.LayoutPaginatedListBinding
import com.example.pagingexample.paging.QuotePagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegularPaginationActivity : AppCompatActivity() {
	private val viewModel by viewModels<RegularQuoteViewModel>()
	private lateinit var binding: LayoutPaginatedListBinding

	private val quotePagingAdapter = QuotePagingAdapter()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = LayoutPaginatedListBinding.inflate(layoutInflater)

		setContentView(binding.root)

		subscribeToEvents()
		initUi()
	}

	private fun initUi() {
		binding.rvQuotes.adapter = quotePagingAdapter.withLoadStateFooter(
			footer = SimpleLoadStateAdapter(
				onRetry = {
					quotePagingAdapter.retry()
				}
			)
		)
	}

	private fun subscribeToEvents() {
		lifecycleScope.launch {
			viewModel.quoteList.flowWithLifecycle(lifecycle)
				.collectLatest { quotes ->
					quotePagingAdapter.submitData(quotes)
				}
		}
		lifecycleScope.launch {
			quotePagingAdapter.loadStateFlow.flowWithLifecycle(lifecycle)
				.collectLatest { state ->
					val isLoading = state.append is LoadState.Loading || state.refresh is LoadState.Loading
					
					binding.pbIsLoading.isVisible = isLoading
				}
		}
	}
}