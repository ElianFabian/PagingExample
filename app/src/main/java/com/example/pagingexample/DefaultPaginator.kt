package com.example.pagingexample

class DefaultPaginator<Key, Item>(
	private val initialKey: Key,
	private inline val onLoadUpdated: (isLoading: Boolean) -> Unit,
	private inline val onRequest: suspend (nextKey: Key) -> Result<List<Item>>,
	private inline val getNextKey: (items: List<Item>) -> Key,
	private inline val onError: (throwable: Throwable?) -> Unit,
	private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit,
) : Paginator<Key, Item> {

	private var currentKey = initialKey
	private var isMakingRequest = false


	override suspend fun loadNextItems() {
		if (isMakingRequest) return

		isMakingRequest = true

		onLoadUpdated(true)

		val result = onRequest(currentKey)

		isMakingRequest = false

		val items = result.getOrElse { throwable ->
			onError(throwable)
			onLoadUpdated(false)
			return
		}

		currentKey = getNextKey(items)
		onSuccess(items, currentKey)

		onLoadUpdated(false)
	}

	override fun reset() {
		currentKey = initialKey
	}
}