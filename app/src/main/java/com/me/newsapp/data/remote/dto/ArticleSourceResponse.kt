package com.me.newsapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ArticleSourceResponse(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)