package com.example.pagingexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagingexample.repository.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegularQuoteViewModel @Inject constructor(
	private val quoteRepository: QuoteRepository,
) : ViewModel() {

	var quoteList = quoteRepository
		.getQuotesFlow()
		.cachedIn(viewModelScope)

	fun jumpToPage(page: Int?) {
		quoteRepository.jumpToPage(page)
	}
}