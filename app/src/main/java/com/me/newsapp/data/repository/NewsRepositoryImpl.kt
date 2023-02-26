package com.me.newsapp.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.me.newsapp.data.local.dao.SourceDao
import com.me.newsapp.data.mapper.toArticle
import com.me.newsapp.data.mapper.toSource
import com.me.newsapp.data.mapper.toSourceEntity
import com.me.newsapp.data.remote.api.NewsApiService
import com.me.newsapp.data.remote.dto.ErrorResponse
import com.me.newsapp.domain.model.Article
import com.me.newsapp.domain.model.Source
import com.me.newsapp.domain.repository.NewsRepository
import com.me.newsapp.utils.DispatcherProvider
import com.me.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService,
    private val sourceDao: SourceDao,
    private val dispatcherProvider: DispatcherProvider,
) : NewsRepository {

    override suspend fun getNewsEverything(
        page: Int?,
        pageSize: Int?,
        q: String?,
        sources: String?,
        apiKey: String,
    ): Flow<Resource<List<Article>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.getEverything(
                    page = page,
                    pageSize = pageSize,
                    q = q,
                    sources = sources,
                    apiKey = apiKey
                )
                response.let { resp ->
                    if (resp.isSuccessful) {
                        val newsEverythingResponse = resp.body()!!
                        emit(
                            Resource.Success(
                                data = newsEverythingResponse.data.map { it.toArticle() })
                        )
                    } else {
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        val err: ErrorResponse =
                            Gson().fromJson(response.errorBody()!!.charStream(), type)
                        emit(Resource.Error(err.message))
                    }
                }
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                emit(Resource.Error(message = "Couldn't load data"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun getNewsSources(
        fetchFromRemote: Boolean,
        category: String,
        apiKey: String,
    ): Flow<Resource<List<Source>>> {
        return flow {
            emit(Resource.Loading(true))
            var localData = sourceDao.getAllByCategory(category)
            if (localData.isNotEmpty()) {
                emit(Resource.Success(data = localData.map {
                    it.toSource()
                }))
            }
            val shouldJustLoadFromCache = localData.isNotEmpty() && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            try {
                val response = api.getSourcesNews(
                    category = category,
                    apiKey = apiKey
                )
                response.let { resp ->
                    if (resp.isSuccessful) {
                        val newsSourcesResponse = resp.body()!!
                        sourceDao.deleteByCategory(category)
                        sourceDao.insertAll(newsSourcesResponse.data.map { it.toSourceEntity() })
                        localData = sourceDao.getAllByCategory(category)
                        emit(Resource.Success(data = localData.map { it.toSource() }))
                    } else {
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        val err: ErrorResponse =
                            Gson().fromJson(response.errorBody()!!.charStream(), type)
                        emit(Resource.Error(err.message))
                    }
                }
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                emit(Resource.Error(message = "Couldn't load data"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun searchNewsSources(
        q: String,
        page: Int,
        pageSize: Int,
    ): Flow<Resource<List<Source>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val offset = pageSize * (page - 1)
                val localData = sourceDao.getSourcesByPage(
                    offset = offset,
                    pageSize = pageSize,
                    name = q
                )
                if (localData.isNotEmpty()) {
                    emit(Resource.Success(data = localData.map {
                        it.toSource()
                    }))
                }
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                emit(Resource.Error(message = "Couldn't load data"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }
}