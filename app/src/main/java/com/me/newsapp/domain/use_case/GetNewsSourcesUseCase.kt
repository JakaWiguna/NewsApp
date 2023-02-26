package com.me.newsapp.domain.use_case

import com.me.newsapp.data.repository.NewsRepositoryImpl
import com.me.newsapp.domain.model.Source
import com.me.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsSourcesUseCase @Inject constructor(private val repository: NewsRepositoryImpl) {
    suspend fun execute(
        fetchFromRemote: Boolean,
        category: String,
        apiKey: String,
    ): Flow<Resource<List<Source>>> {
        return repository.getNewsSources(
            fetchFromRemote = fetchFromRemote,
            category = category,
            apiKey = apiKey
        )
    }
}