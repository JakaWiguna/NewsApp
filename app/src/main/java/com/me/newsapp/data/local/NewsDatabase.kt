package com.me.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.me.newsapp.data.local.dao.SourceDao
import com.me.newsapp.data.local.entity.SourceEntity

@Database(
    entities = [
        SourceEntity::class],
    version = 1,
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val sourceDao: SourceDao

    companion object {
        const val DATABASE_NAME = "news_db"
    }
}