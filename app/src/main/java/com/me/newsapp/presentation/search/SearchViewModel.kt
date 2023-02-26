package com.me.newsapp.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.newsapp.BuildConfig
import com.me.newsapp.domain.use_case.GetNewsEverythingUseCase
import com.me.newsapp.domain.use_case.SearchNewsSourcesUseCase
import com.me.newsapp.utils.Constants.ITEMS_PER_PAGE
import com.me.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getNewsEverythingUseCase: GetNewsEverythingUseCase,
    private val searchNewsSourcesUseCase: SearchNewsSourcesUseCase,
) : ViewModel() {

    var state by mutableStateOf(SearchState())

    private var searchJob: Job? = null

    private val _event = MutableSharedFlow<SearchEvent>()
    val event: SharedFlow<SearchEvent> get() = _event

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChange -> {
                searchJob?.cancel()
                state = state.copy(
                    searchQuery = event.query,
                    page = 1,
                    pageSize = ITEMS_PER_PAGE,
                    articles = emptyList(),
                    sources = emptyList(),
                )
                if (state.mode == ModeSearch.NEWS_ARTICLES) {
                    searchJob = viewModelScope.launch {
                        delay(2000L)
                        doGetNewsEverythingWithLoadMore(
                            q = event.query,
                            page = state.page,
                            pageSize = state.pageSize
                        )
                    }
                } else {
                    searchJob = viewModelScope.launch {
                        delay(2000L)
                        doSearchSources(
                            q = event.query,
                            page =  state.page,
                            pageSize = state.pageSize
                        )
                    }
                }
            }
            is SearchEvent.OnLoadMore -> {
                if (state.mode == ModeSearch.NEWS_ARTICLES) {
                    state = state.copy(
                        page = state.page.plus(1),
                        pageSize = ITEMS_PER_PAGE,
                    )
                    doGetNewsEverythingWithLoadMore(
                        q = state.searchQuery,
                        page = state.page,
                        pageSize = state.pageSize,
                    )
                } else {
                    state = state.copy(
                        page = state.page.plus(1),
                        pageSize = ITEMS_PER_PAGE,
                    )
                    doSearchSources(
                        q = state.searchQuery,
                        page = state.page,
                        pageSize = state.pageSize,
                    )
                }
            }
            is SearchEvent.OnSearchModeChange -> {
                state = state.copy(
                    mode = if (event.mode.lowercase() == "articles") ModeSearch.NEWS_ARTICLES else ModeSearch.NEWS_SOURCES
                )
            }
            is SearchEvent.ShowToast -> Unit
        }
    }

    private fun doGetNewsEverythingWithLoadMore(
        page: Int? = null,
        pageSize: Int? = null,
        q: String? = null,
        sources: String? = null,
    ) {
        if (!q.isNullOrBlank() || !sources.isNullOrBlank()) {
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
                                    _event.emit(SearchEvent.ShowToast(message))
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
                                    _event.emit(SearchEvent.ShowToast(message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun doSearchSources(
        q: String,
        page: Int,
        pageSize: Int,
    ) {
        if (q.isNotBlank()) {
            viewModelScope.launch {
                searchNewsSourcesUseCase.execute(
                    q = q,
                    page = page,
                    pageSize = pageSize,
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
                                        sources = data,
                                    )
                                }
                            }
                            is Resource.Error -> {
                                resource.message?.let { message ->
                                    _event.emit(SearchEvent.ShowToast(message))
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
                                        sources = state.sources + data
                                    )
                                }
                            }
                            is Resource.Error -> {
                                resource.message?.let { message ->
                                    _event.emit(SearchEvent.ShowToast(message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
