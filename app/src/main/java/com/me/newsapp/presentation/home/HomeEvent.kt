package com.me.newsapp.presentation.home

sealed class HomeEvent {
    data class ShowToast(val message: String) : HomeEvent()
}