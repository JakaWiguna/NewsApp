package com.me.newsapp.presentation.home

data class HomeState(
    val isLoading: Boolean = true,
    val categories: List<String> = listOf(
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology"
    ),
)
