package com.example.pagingexample.retrofit

import com.example.pagingexample.models.QuoteList
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
	@GET("/quotes")
	suspend fun getQuotes(
		@Query("page") page: Int
	): QuoteList
}