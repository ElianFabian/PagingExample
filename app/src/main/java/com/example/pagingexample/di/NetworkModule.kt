package com.example.pagingexample.di

import com.example.pagingexample.retrofit.QuoteApi
import com.example.pagingexample.utils.QUOTABLE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	@Provides
	@Singleton
	fun provideRetrofit() = Retrofit.Builder()
		.baseUrl(QUOTABLE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	@Provides
	@Singleton
	fun provideQuoteApi(retrofit: Retrofit): QuoteApi = retrofit.create(QuoteApi::class.java)
}