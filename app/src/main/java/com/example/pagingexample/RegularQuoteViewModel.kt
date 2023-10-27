package com.example.pagingexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagingexample.models.QuoteModel
import com.example.pagingexample.repository.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegularQuoteViewModel @Inject constructor(
	private val quoteRepository: QuoteRepository,
) : ViewModel() {
	
	init {
		viewModelScope.launch { 
			_stickyQuote.value = quoteRepository.getQuote(StickyQuoteId)
		}
	}

	var quoteList = quoteRepository
		.getQuotesFlow()
		.cachedIn(viewModelScope)

	fun jumpToPage(page: Int?) {
		quoteRepository.jumpToPage(page)
	}
	
	private val _stickyQuote = MutableStateFlow<QuoteModel?>(null)
	val stickyQuote = _stickyQuote
	
	
	companion object {
		const val StickyQuoteId = "wiVouCxUSmn"
	}
}