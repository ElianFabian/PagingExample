package com.example.pagingexample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.pagingexample.databinding.LayoutPaginatedListBinding
import com.example.pagingexample.paging.QuotePagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
		binding.apply {
			rvQuotes.adapter = quotePagingAdapter.withLoadStateFooter(
				footer = SimpleLoadStateAdapter(
					onRetry = {
						quotePagingAdapter.retry()
					}
				)
			)

			btnRefresh.setOnClickListener {
				val newPage = binding.tietPage.text.toString().toIntOrNull()
				viewModel.jumpToPage(newPage)
				quotePagingAdapter.refresh()
			}
		}
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

					binding.pbIsLoading.isInvisible = !isLoading
				}
		}
		lifecycleScope.launch {
			quotePagingAdapter.loadStateFlow.flowWithLifecycle(lifecycle)
				.map { it.refresh }
				.distinctUntilChanged()
				.collectLatest { refresh ->
					if (refresh is LoadState.NotLoading) {
						binding.rvQuotes.scrollToPosition(0)
					}
				}
		}
	}
}