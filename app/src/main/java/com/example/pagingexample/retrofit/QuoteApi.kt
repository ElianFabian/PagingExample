package com.example.pagingexample.retrofit

import com.example.pagingexample.models.QuoteList
import com.example.pagingexample.models.QuoteModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuoteApi {
	@GET("/quotes")
	suspend fun getQuotes(
		@Query("page")
		page: Int
	): QuoteList

	@GET("/quotes/{id}")
	suspend fun getQuote(
		@Path("id")
		id: String
	): QuoteModel
}