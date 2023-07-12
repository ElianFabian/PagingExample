package com.example.pagingexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pagingexample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setContentView(binding.root)

		initUi()
	}

	private fun initUi() {
		binding.apply {
			btnRegularPagination.setOnClickListener {
				startActivity(
					Intent(applicationContext, RegularPaginationActivity::class.java)
				)
			}
			btnCustomPagination.setOnClickListener {
				startActivity(
					Intent(applicationContext, CustomPaginationActivity::class.java)
				)
			}
		}
	}

}