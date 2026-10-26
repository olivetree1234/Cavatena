package com.example.violinpracticeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {

    @Insert
    suspend fun insertAll(items: List<LibraryItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(
        items: List<LibraryItemEntity>
    ): List<Long>

    @Query("SELECT * FROM library_items ORDER BY category, title")
    fun getAllLibraryItems(): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE category = :category ORDER BY title")
    fun getItemsByCategory(category: String): Flow<List<LibraryItemEntity>>

    @Query("SELECT COUNT(*) FROM library_items")
    suspend fun getLibraryCount(): Int

    @Query("""
        SELECT * FROM library_items
        WHERE category = :category
          AND (title LIKE '%' || :keyword || '%'
               OR composer LIKE '%' || :keyword || '%'
               OR notes LIKE '%' || :keyword || '%')
        ORDER BY title
    """)
    suspend fun searchLibraryItems(
        category: String,
        keyword: String
    ): List<LibraryItemEntity>

    @Query("SELECT COUNT(*) FROM library_items WHERE category = :category")
    suspend fun countItemsByCategory(category: String): Int

    @Query("SELECT * FROM library_items WHERE category = :category ORDER BY title")
    suspend fun getItemsByCategoryForCoach(category: String): List<LibraryItemEntity>

    @Query("""
        SELECT * FROM library_items
        WHERE title LIKE '%' || :keyword || '%'
           OR composer LIKE '%' || :keyword || '%'
           OR notes LIKE '%' || :keyword || '%'
        ORDER BY title
    """)
    suspend fun searchLibraryByKeyword(keyword: String): List<LibraryItemEntity>
}
