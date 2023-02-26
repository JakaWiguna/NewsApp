package com.me.newsapp.domain.repository

import com.me.newsapp.domain.model.Article
import com.me.newsapp.domain.model.Source
import com.me.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getNewsSources(
        fetchFromRemote:Boolean,
        category: String,
        apiKey: String,
    ): Flow<Resource<List<Source>>>

    suspend fun getNewsEverything(
        page: Int? = null,
        pageSize: Int? = null,
        q: String? = null,
        sources: String? = null,
        apiKey: String,
    ): Flow<Resource<List<Article>>>
}