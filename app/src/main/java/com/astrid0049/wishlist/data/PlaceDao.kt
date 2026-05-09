package com.astrid0049.wishlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    // Wishlist: oldest pinned first
    @Query("SELECT * FROM places WHERE visitedAt IS NULL ORDER BY dateAdded ASC")
    fun getActiveByPining(): Flow<List<Place>>

    // Wishlist: newest pinned first
    @Query("SELECT * FROM places WHERE visitedAt IS NULL ORDER BY dateAdded DESC")
    fun getActiveByRecent(): Flow<List<Place>>

    // Visited shelf
    @Query("SELECT * FROM places WHERE visitedAt IS NOT NULL ORDER BY visitedAt DESC")
    fun getVisited(): Flow<List<Place>>

    // Detail screen
    @Query("SELECT * FROM places WHERE id = :id")
    fun getById(id: Int): Flow<Place?>

    @Insert
    suspend fun insert(place: Place): Long

    @Update
    suspend fun update(place: Place)

    @Query("UPDATE places SET visitedAt = :timestamp WHERE id = :id")
    suspend fun visit(id: Int, timestamp: Long)

    @Query("UPDATE places SET visitedAt = NULL WHERE id = :id")
    suspend fun unvisit(id: Int)

    @Delete
    suspend fun hardDelete(place: Place)

    // Reality Check stats
    @Query("SELECT COUNT(*) FROM places WHERE visitedAt IS NULL")
    fun countActive(): Flow<Int>

    @Query("SELECT COUNT(*) FROM places WHERE visitedAt IS NOT NULL")
    fun countVisited(): Flow<Int>

    @Query("SELECT * FROM places WHERE visitedAt IS NULL ORDER BY dateAdded ASC LIMIT 1")
    fun longestPining(): Flow<Place?>

    @Query("SELECT * FROM places WHERE visitedAt IS NOT NULL ORDER BY visitedAt DESC LIMIT 1")
    fun lastVisited(): Flow<Place?>

    @Query("""
        SELECT * FROM places
        WHERE visitedAt IS NOT NULL
        ORDER BY (visitedAt - dateAdded) DESC
        LIMIT 1
    """)
    fun longestWait(): Flow<Place?>

    @Query("SELECT IFNULL(AVG(visitedAt - dateAdded), 0) FROM places WHERE visitedAt IS NOT NULL")
    fun avgWaitMs(): Flow<Long>

    @Query("""
        SELECT category AS category, COUNT(*) AS count
        FROM places
        WHERE visitedAt IS NULL
        GROUP BY category
    """)
    fun categoryCounts(): Flow<List<CategoryCount>>
}