package com.me.newsapp.presentation.news_articles

import com.me.newsapp.domain.model.Article

data class NewsArticlesState(
    val isLoading: Boolean = false,

    val articles: List<Article> = emptyList(),
    val sources: String = "",

    val page: Int = 0,
    val pageSize: Int = 20,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
)
