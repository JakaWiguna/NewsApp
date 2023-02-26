package com.me.newsapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsSourcesResponse(

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("sources")
    val data: List<SourceResponse>

)
