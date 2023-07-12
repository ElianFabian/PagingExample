package com.example.pagingexample

interface Paginator<Key, Item> {
	suspend fun loadNextItems()
	fun reset()
}