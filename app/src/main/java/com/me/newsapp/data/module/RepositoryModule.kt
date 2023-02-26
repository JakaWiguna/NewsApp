package com.me.newsapp.data.module

import android.app.Application
import androidx.room.Room
import com.me.newsapp.data.local.NewsDatabase
import com.me.newsapp.data.local.dao.SourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NewsDatabase {
        return Room.databaseBuilder(
            app,
            NewsDatabase::class.java,
            NewsDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideSourceDao(db: NewsDatabase): SourceDao {
        return db.sourceDao
    }
}