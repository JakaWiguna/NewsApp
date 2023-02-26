package com.me.newsapp.domain.use_case

import com.me.newsapp.data.repository.NewsRepositoryImpl
import com.me.newsapp.domain.model.Article
import com.me.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsEverythingUseCase @Inject constructor(private val repository: NewsRepositoryImpl) {
    suspend fun execute(
        page: Int? = null,
        pageSize: Int? = null,
        q: String? = null,
        sources: String? = null,
        apiKey: String,
    ): Flow<Resource<List<Article>>> {
        return repository.getNewsEverything(
            page = page,
            pageSize = pageSize,
            q = q,
            sources = sources,
            apiKey = apiKey
        )
    }
}