package com.me.newsapp.data.module

import com.me.newsapp.utils.DefaultDispatcherProvider
import com.me.newsapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun bindDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

}