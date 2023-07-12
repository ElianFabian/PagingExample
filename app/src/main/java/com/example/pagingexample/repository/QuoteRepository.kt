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

class QuoteRepository @Inject constructor(
	private val quoteApi: QuoteApi,
) {
	fun getQuotes() = Pager(
		config = PagingConfig(pageSize = 20, maxSize = 100),
		pagingSourceFactory = { QuotePagingSource(quoteApi) },
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