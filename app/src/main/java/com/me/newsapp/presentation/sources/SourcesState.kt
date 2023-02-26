package com.me.newsapp.presentation.sources

import com.me.newsapp.domain.model.Source

data class SourcesState(
    val isLoading: Boolean = false,
    val sources: List<Source> = emptyList(),
)