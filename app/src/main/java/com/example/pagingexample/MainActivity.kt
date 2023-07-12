package com.example.pagingexample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pagingexample.databinding.ActivityMainBinding
import com.example.pagingexample.paging.QuotePagingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
	private val viewModel by viewModels<QuoteViewModel>()
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setContentView(binding.root)

		initUi()
	}

	private fun initUi() {
		val pagingAdapter = QuotePagingAdapter()
		binding.rvQuotes.adapter = pagingAdapter

		viewModel.quoteList.observe(this)
		{
			pagingAdapter.submitData(lifecycle, it)
		}
	}
}