package com.me.newsapp.domain.model

data class Article(
    val publishedAt: String,
    val author: String,
    val urlToImage: String,
    val description: String,
    val source: ArticleSource,
    val title: String,
    val url: String,
    val content: String
)



