package com.me.newsapp.data.remote.api

import com.me.newsapp.data.remote.dto.NewsArticlesResponse
import com.me.newsapp.data.remote.dto.NewsSourcesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines/sources")
    suspend fun getSourcesNews(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String,
    ): Response<NewsSourcesResponse>

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("q") q: String? = null,
        @Query("sources") sources: String? = null,
        @Query("apiKey") apiKey: String,
    ): Response<NewsArticlesResponse>

}