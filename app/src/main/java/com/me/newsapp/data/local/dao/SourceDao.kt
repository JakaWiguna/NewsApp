package com.me.newsapp.data.local.dao

import androidx.room.*
import com.me.newsapp.data.local.entity.SourceEntity

@Dao
interface SourceDao {

    @Transaction
    @Query("SELECT * FROM SourceEntity WHERE name LIKE '%' || :name || '%' LIMIT :pageSize OFFSET :offset")
    fun getSourcesByPage(pageSize: Int, offset: Int, name: String): List<SourceEntity>

    @Transaction
    @Query("SELECT * FROM SourceEntity WHERE category = :category")
    fun getAllByCategory(category: String): List<SourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<SourceEntity>)

    @Query("DELETE FROM SourceEntity")
    suspend fun deleteAll()

    @Query("DELETE FROM SourceEntity WHERE category = :category")
    suspend fun deleteByCategory(category: String)

}




