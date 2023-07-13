package com.example.pagingexample.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.pagingexample.models.QuoteModel
import com.example.pagingexample.paging.QuotePagingSource
import com.example.pagingexample.retrofit.QuoteApi
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PaginationState(var currentPagePosition: Int?)

class QuoteRepository @Inject constructor(
	private val quoteApi: QuoteApi,
) {
	private val pageState = PaginationState(currentPagePosition = null)

	fun jumpToPage(page: Int?) {
		pageState.currentPagePosition = page
	}

	private fun createPagingSource(): QuotePagingSource {
		return QuotePagingSource(quoteApi, initialPage = 1, pageState = pageState)
	}

	fun getQuotesFlow() = Pager(
		config = PagingConfig(pageSize = 20, maxSize = 100),
		pagingSourceFactory = { createPagingSource() },
	).flow

	suspend fun getQuotes(page: Int): Result<List<QuoteModel>> {
		return try {
			val response = quoteApi.getQuotes(page)

			Result.success(response.results)
		}
		catch (e: IOException) {
			Result.failure(e)
		}
		catch (e: HttpException) {
			Result.failure(e)
		}
		catch (e: CancellationException) {
			throw e
		}
		catch (e: Exception) {
			Result.failure(e)
		}
	}
}