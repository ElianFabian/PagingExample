package com.example.pagingexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagingexample.models.QuoteModel
import com.example.pagingexample.repository.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomQuoteViewModel @Inject constructor(
	private val repository: QuoteRepository,
) : ViewModel() {

	private val _state = MutableStateFlow(CustomQuoteState())
	val state = _state.asStateFlow()

	private val paginator: Paginator<Int, QuoteModel> = DefaultPaginator(
		initialKey = _state.value.currentPage,
		onLoadUpdated = { isLoading ->
			_state.value = _state.value.copy(isLoading = isLoading)
		},
		onRequest = { nextPage ->
			repository.getQuotes(nextPage)
		},
		getNextKey = {
			state.value.currentPage + 1
		},
		onError = { throwable ->
			_state.value = _state.value.copy(
				error = throwable?.localizedMessage
			)
		},
		onSuccess = { items, nextPage ->
			_state.value = _state.value.copy(
				items = state.value.items + items,
				currentPage = nextPage,
				isEndReached = items.isEmpty(),
			)
		},
	)

	val quoteList = repository
		.getQuotesFlow()
		.cachedIn(viewModelScope)

	init {
		viewModelScope.launch {
			loadNextItems()
		}
	}

	fun loadNextItems() {
		viewModelScope.launch {
			paginator.loadNextItems()
		}
	}
}

data class CustomQuoteState(
	val isLoading: Boolean = false,
	val items: List<QuoteModel> = emptyList(),
	val error: String? = null,
	val isEndReached: Boolean = false,
	val currentPage: Int = 0,
)