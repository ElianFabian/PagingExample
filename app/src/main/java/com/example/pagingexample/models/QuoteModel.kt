package com.example.pagingexample.models

import com.google.gson.annotations.SerializedName

data class QuoteModel(
	@SerializedName("_id")
	val id: String,
	@SerializedName("author")
	val author: String,
	@SerializedName("authorSlug")
	val authorSlug: String,
	@SerializedName("content")
	val content: String,
	@SerializedName("dateAdded")
	val dateAdded: String,
	@SerializedName("dateModified")
	val dateModified: String,
	@SerializedName("length")
	val length: Int,
	@SerializedName("tags")
	val tags: List<String>,
)