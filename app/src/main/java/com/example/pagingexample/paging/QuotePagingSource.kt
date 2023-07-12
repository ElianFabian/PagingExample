package com.example.pagingexample.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pagingexample.models.QuoteModel
import com.example.pagingexample.retrofit.QuoteApi
import kotlinx.coroutines.delay

class QuotePagingSource(
	private val quoteApi: QuoteApi,
) : PagingSource<Int, QuoteModel>() {
	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteModel> {
		return try {
			val position = params.key ?: 1

			val response = quoteApi.getQuotes(position)

			LoadResult.Page(
				data = response.results,
				prevKey = if (position == 1) null else position - 1,
				nextKey = if (position == response.totalPages) null else position + 1,
			)
		}
		catch (e: Exception) {
			LoadResult.Error(e)
		}
	}

	override fun getRefreshKey(state: PagingState<Int, QuoteModel>): Int? {

		val anchorPosition = state.anchorPosition ?: return null

		return state.closestPageToPosition(anchorPosition)?.run {
			prevKey?.plus(1) ?: nextKey?.minus(1)
		}
	}
}