package com.me.newsapp.data.mapper

import com.me.newsapp.data.local.entity.SourceEntity
import com.me.newsapp.data.remote.dto.SourceResponse
import com.me.newsapp.domain.model.Source

fun SourceResponse.toSourceEntity(): SourceEntity {
    return SourceEntity(
        id = id ?: "",
        name = name ?: "",
        description = description ?: "",
        language = language ?: "",
        country = country ?: "",
        category = category ?: "",
        url = url ?: ""
    )
}

fun SourceEntity.toSource(): Source {
    return Source(
        id = id,
        name = name,
        description = description,
        language = language,
        country = country,
        category = category,
        url = url
    )
}