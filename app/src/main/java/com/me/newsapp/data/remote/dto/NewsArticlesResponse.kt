package com.me.newsapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsArticlesResponse(

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("totalResults")
	val totalResults: Int = 0,

	@field:SerializedName("articles")
	val data: List<ArticleResponse>,
)