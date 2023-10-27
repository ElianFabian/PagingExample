package com.example.pagingexample

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingexample.databinding.LayoutPaginatedListBinding
import com.example.pagingexample.models.QuoteModel
import com.example.pagingexample.paging.QuotePagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class RegularPaginationActivity : AppCompatActivity() {
	private val viewModel by viewModels<RegularQuoteViewModel>()
	private lateinit var binding: LayoutPaginatedListBinding

	private val quotePagingAdapter = QuotePagingAdapter()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = LayoutPaginatedListBinding.inflate(layoutInflater)

		setContentView(binding.root)

		subscribeToEvents()
		initUi()
	}


	private fun initUi() {
		binding.apply {
			rvQuotes.adapter = quotePagingAdapter.withLoadStateFooter(
				footer = SimpleLoadStateAdapter(
					onRetry = {
						quotePagingAdapter.retry()
					}
				)
			)

			btnRefresh.setOnClickListener {
				val newPage = binding.tietPage.text.toString().toIntOrNull()
				viewModel.jumpToPage(newPage)
				quotePagingAdapter.refresh()
			}
		}
	}

	private fun subscribeToEvents() {
		lifecycleScope.launch {
			viewModel.quoteList.flowWithLifecycle(lifecycle)
				.collectLatest { quotes ->
					quotePagingAdapter.submitData(quotes)
				}
		}
		lifecycleScope.launch {
			quotePagingAdapter.loadStateFlow.flowWithLifecycle(lifecycle)
				.collectLatest { state ->
					val isLoading = state.append is LoadState.Loading || state.refresh is LoadState.Loading

					binding.pbIsLoading.isInvisible = !isLoading
				}
		}
		lifecycleScope.launch {
			quotePagingAdapter.loadStateFlow.flowWithLifecycle(lifecycle)
				.map { it.refresh }
				.distinctUntilChanged()
				.collectLatest { refresh ->
					if (refresh is LoadState.NotLoading) {
						binding.rvQuotes.scrollToPosition(0)
					}
				}
		}
		lifecycleScope.launch { 
			viewModel.stickyQuote.filterNotNull()
				.collectLatest { quote ->
					binding.viewStickyQuote.apply { 
						tvQuote.text = quote.content
						tvAuthor.text = quote.author
					}
				}
		}
		lifecycleScope.launch {
			
			val stickyQuote = viewModel.stickyQuote.filterNotNull().first()

			binding.rvQuotes.itemVisibilityFlow(
				itemKey = stickyQuote.id,
				getItemKey = { quote ->
					quote.id
				},
				getItem = { position ->
					quotePagingAdapter.snapshot()[position]
				},
			).map {
				if (it) 0F else 1F
			}.animate(
				speed = 0.01F,
			).collectLatest { alpha ->
				binding.viewStickyQuoteContainer.alpha = alpha
			}
		}
	}
}

fun <T : Any, K : Any> RecyclerView.itemVisibilityFlow(
	itemKey: K,
	getItemKey: (item: T) -> K,
	adapter: PagingDataAdapter<T, *>,
): Flow<Boolean> {
	return callbackFlow {
		val scrollListener = object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

				val layoutManager = recyclerView.layoutManager

				check(layoutManager is LinearLayoutManager) {
					"To use RecyclerView.itemVisibilityFlow RecyclerView must have a LinearLayoutManager."
				}

				val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
				val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

				var isVisible = false
				for (position in firstVisiblePosition..lastVisiblePosition) {
					val item = adapter.snapshot()[position] ?: continue

					isVisible = getItemKey(item) == itemKey

					if (isVisible) {
						break
					}
				}

				trySend(isVisible)
			}
		}

		addOnScrollListener(scrollListener)

		awaitClose {
			removeOnScrollListener(scrollListener)
		}
	}.distinctUntilChanged()
}

fun <T : Any, K : Any> RecyclerView.itemVisibilityFlow(
	itemKey: K,
	getItemKey: (item: T) -> K,
	adapter: ListAdapter<T, *>,
): Flow<Boolean> {
	return callbackFlow {
		val scrollListener = object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

				val layoutManager = recyclerView.layoutManager

				check(layoutManager is LinearLayoutManager) {
					"To use RecyclerView.itemVisibilityFlow RecyclerView must have a LinearLayoutManager."
				}

				val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
				val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
				var isVisible = false

				for (position in firstVisiblePosition..lastVisiblePosition) {
					val item = adapter.currentList[position] ?: continue

					isVisible = getItemKey(item) == itemKey

					if (isVisible) {
						break
					}
				}

				trySend(isVisible)
			}
		}

		addOnScrollListener(scrollListener)

		awaitClose {
			removeOnScrollListener(scrollListener)
		}
	}.distinctUntilChanged()
}

fun <T : Any, K : Any> RecyclerView.itemVisibilityFlow(
	itemKey: K,
	getItemKey: (item: T) -> K,
	getItem: (position: Int) -> T?,
): Flow<Boolean> {
	return callbackFlow {
		val scrollListener = object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

				val layoutManager = recyclerView.layoutManager

				check(layoutManager is LinearLayoutManager) {
					"To use RecyclerView.itemVisibilityFlow RecyclerView must have a LinearLayoutManager."
				}

				val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
				val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
				var isVisible = false

				for (position in firstVisiblePosition..lastVisiblePosition) {
					val item = getItem(position) ?: continue

					isVisible = getItemKey(item) == itemKey

					if (isVisible) {
						break
					}
				}

				trySend(isVisible)
			}
		}

		addOnScrollListener(scrollListener)

		awaitClose {
			removeOnScrollListener(scrollListener)
		}
	}.distinctUntilChanged()
}


@JvmName("animateFloat")
fun Flow<Float>.animate(
	speed: Float,
	interpolator: TimeInterpolator = LinearInterpolator(),
): Flow<Float> = callbackFlow {
	var previousValue: Float? = null

	val animator = ValueAnimator().apply {
		this.interpolator = interpolator
	}

	val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
		trySend(animation.animatedValue as Float)
	}
	animator.addUpdateListener(updateListener)

	collect { newValue ->

		if (previousValue == null) {
			send(newValue)
		}
		else if (animator.isRunning) {
			val currentAnimatedValue = animator.animatedValue as Float
			val distance = abs(newValue - currentAnimatedValue)
			val durationInMillis = (distance / speed).toLong()

			animator.apply {
				setFloatValues(currentAnimatedValue, newValue)
				this.duration = durationInMillis
				animator.start()
			}
		}
		else {
			animator.apply {
				val duration = if (previousValue != null) {
					val distance = abs(newValue - previousValue!!)
					(distance / speed).toLong()
				}
				else 0L

				setFloatValues(previousValue!!, newValue)
				this.duration = duration
				animator.start()
			}
		}
		previousValue = newValue
	}

	awaitClose {
		animator.removeUpdateListener(updateListener)
		animator.cancel()
	}
}.distinctUntilChanged()