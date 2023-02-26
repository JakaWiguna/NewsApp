package com.me.newsapp.presentation.search

import com.me.newsapp.domain.model.Article

data class SearchState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val mode: ModeSearch = ModeSearch.NEWS_ARTICLES,
    val articles: List<Article> = emptyList(),

    val page: Int = 0,
    val pageSize: Int = 20,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
)

enum class ModeSearch {
    NEWS_ARTICLES,
    NEWS_SOURCES,
}