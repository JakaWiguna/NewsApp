package com.me.newsapp.data.mapper

import com.me.newsapp.data.remote.dto.ArticleSourceResponse
import com.me.newsapp.domain.model.ArticleSource


fun ArticleSourceResponse.toArticleSource(): ArticleSource {
    return ArticleSource(
        id = id ?: "",
        name = name ?: ""
    )
}
