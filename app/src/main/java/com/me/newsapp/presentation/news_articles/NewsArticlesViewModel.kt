package com.me.newsapp.presentation.news_articles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.newsapp.BuildConfig
import com.me.newsapp.domain.use_case.GetNewsEverythingUseCase
import com.me.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsArticlesViewModel @Inject constructor(
    private val getNewsEverythingUseCase: GetNewsEverythingUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var state by mutableStateOf(NewsArticlesState())

    private val _event = MutableSharedFlow<NewsArticlesEvent>()
    val event: SharedFlow<NewsArticlesEvent> get() = _event

    private val sources: String = checkNotNull(savedStateHandle["sources"])

    fun onEvent(event: NewsArticlesEvent) {
        when (event) {
            is NewsArticlesEvent.OnLoadMore -> {
                state = state.copy(
                    page = state.page.plus(1),
                    pageSize = 10,
                    sources = sources
                )
                doGetNewsEverythingWithLoadMore(
                    page = state.page,
                    pageSize = state.pageSize,
                    sources = state.sources
                )
            }
            is NewsArticlesEvent.ShowToast -> Unit
        }
    }

    init {
        doRefresh()
    }

    fun doRefresh() {
        state = state.copy(
            page = 1,
            pageSize = 10,
            sources = sources
        )
        doGetNewsEverythingWithLoadMore(
            page = state.page,
            pageSize = state.pageSize,
            sources = state.sources
        )
    }

    private fun doGetNewsEverythingWithLoadMore(
        page: Int? = null,
        pageSize: Int? = null,
        q: String? = null,
        sources: String? = null,
    ) {
        viewModelScope.launch {
            getNewsEverythingUseCase.execute(
                page = page,
                pageSize = pageSize,
                q = q,
                sources = sources,
                apiKey = BuildConfig.API_KEY
            ).collect { resource ->
                if (page == 1) {
                    when (resource) {
                        is Resource.Loading -> {
                            resource.isLoading.let { isLoading ->
                                state = state.copy(
                                    isLoading = isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            resource.data?.let { data ->
                                state = state.copy(
                                    canLoadMore = state.pageSize == data.size,
                                    articles = data,
                                )
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(NewsArticlesEvent.ShowToast(message))
                            }
                        }
                    }
                } else {
                    when (resource) {
                        is Resource.Loading -> {
                            resource.isLoading.let { isLoadingMore ->
                                state = state.copy(
                                    isLoadingMore = isLoadingMore
                                )
                            }
                        }
                        is Resource.Success -> {
                            resource.data?.let { data ->
                                state = state.copy(
                                    canLoadMore = state.pageSize == data.size,
                                    articles = state.articles + data
                                )
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(NewsArticlesEvent.ShowToast(message))
                            }
                        }
                    }
                }
            }
        }
    }
}
