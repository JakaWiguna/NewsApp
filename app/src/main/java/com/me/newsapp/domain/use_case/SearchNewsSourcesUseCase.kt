package com.me.newsapp.domain.use_case

import com.me.newsapp.data.repository.NewsRepositoryImpl
import com.me.newsapp.domain.model.Source
import com.me.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsSourcesUseCase @Inject constructor(private val repository: NewsRepositoryImpl) {
    suspend fun execute(
        q: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Source>>> {
        return repository.searchNewsSources(
            q = q,
            page = page,
            pageSize = pageSize
        )
    }
}