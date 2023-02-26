package com.me.newsapp.presentation.search

sealed class SearchEvent {
    data class ShowToast(val message: String) : SearchEvent()
    data class OnSearchQueryChange(val query: String): SearchEvent()
    data class OnSearchModeChange(val mode: String): SearchEvent()
    object OnLoadMore : SearchEvent()
}