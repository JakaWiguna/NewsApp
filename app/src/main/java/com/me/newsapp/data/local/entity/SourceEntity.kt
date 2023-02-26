package com.me.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SourceEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val country: String,
    val description: String,
    val language: String,
    val category: String,
    val url: String,
)