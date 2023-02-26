package com.me.newsapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ArticleResponse(

    @field:SerializedName("publishedAt")
	val publishedAt: Date,

    @field:SerializedName("author")
	val author: String? = null,

    @field:SerializedName("urlToImage")
	val urlToImage: String? = null,

    @field:SerializedName("description")
	val description: String? = null,

    @field:SerializedName("source")
	val source: ArticleSourceResponse,

    @field:SerializedName("title")
	val title: String? = null,

    @field:SerializedName("url")
	val url: String? = null,

    @field:SerializedName("content")
	val content: String? = null
)


