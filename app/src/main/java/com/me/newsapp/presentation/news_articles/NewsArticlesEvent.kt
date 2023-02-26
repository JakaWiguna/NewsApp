package com.me.newsapp.presentation.news_articles

sealed class NewsArticlesEvent {
    data class ShowToast(val message: String) : NewsArticlesEvent()
    object OnLoadMore : NewsArticlesEvent()
}