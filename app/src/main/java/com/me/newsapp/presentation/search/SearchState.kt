package com.me.newsapp.presentation.search

import com.me.newsapp.domain.model.Article
import com.me.newsapp.domain.model.Source
import com.me.newsapp.utils.Constants.ITEMS_PER_PAGE

data class SearchState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val mode: ModeSearch = ModeSearch.NEWS_ARTICLES,
    val articles: List<Article> = emptyList(),
    val sources: List<Source> = emptyList(),

    val page: Int = 1,
    val pageSize: Int = ITEMS_PER_PAGE,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
)

enum class ModeSearch {
    NEWS_ARTICLES,
    NEWS_SOURCES,
}