package com.astrid0049.wishlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String? = null,
    val category: String,
    val notes: String? = null,
    val dateAdded: Long,
    val lastUpdated: Long,
    val visitedAt: Long? = null
)
