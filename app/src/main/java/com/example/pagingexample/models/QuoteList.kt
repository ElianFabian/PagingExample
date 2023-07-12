package com.example.pagingexample.models

data class QuoteList(
	val count: Int,
	val lastItemIndex: Int,
	val page: Int,
	val results: List<QuoteModel>,
	val totalCount: Int,
	val totalPages: Int,
)
