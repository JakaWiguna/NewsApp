package com.me.newsapp.data.mapper

import com.me.newsapp.data.remote.dto.ArticleResponse
import com.me.newsapp.domain.model.Article
import com.me.newsapp.utils.CommonUtils.Companion.formatDate

fun ArticleResponse.toArticle(): Article {
    return Article(
        publishedAt = formatDate(publishedAt),
        author = author ?: "",
        urlToImage = urlToImage
            ?: "https://www.slntechnologies.com/wp-content/uploads/2017/08/ef3-placeholder-image.jpg",
        description = description ?: "",
        source = source.toArticleSource(),
        title = title ?: "",
        url = url ?: "",
        content = content ?: ""
    )
}