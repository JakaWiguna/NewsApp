package com.me.newsapp.presentation.sources

sealed class SourcesEvent {
    data class ShowToast(val message: String) : SourcesEvent()
}