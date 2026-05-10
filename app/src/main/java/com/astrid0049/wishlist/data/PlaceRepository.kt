package com.astrid0049.wishlist.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class PlaceRepository(
    private val dao: PlaceDao,
    private val preferences: UserPreferencesRepository
) {
    val sortOrder: Flow<String> = preferences.sortOrder
    val viewMode: Flow<String> = preferences.viewMode

    @OptIn(ExperimentalCoroutinesApi::class)
    fun activePlaces(): Flow<List<Place>> {
        return sortOrder.flatMapLatest { order ->
            when (order) {
                "recent" -> dao.getActiveByRecent()
                else -> dao.getActiveByPining()
            }
        }
    }

    fun visitedPlaces(): Flow<List<Place>> {
        return dao.getVisited()
    }

    fun getById(id: Int): Flow<Place?> {
        return dao.getById(id)
    }

    suspend fun addPlace(
        name: String,
        location: String?,
        category: String,
        notes: String?
    ) {
        val now = System.currentTimeMillis()

        dao.insert(
            Place(
                name = name,
                location = location,
                category = category,
                notes = notes,
                dateAdded = now,
                lastUpdated = now,
                visitedAt = null
            )
        )
    }

    suspend fun updatePlace(place: Place) {
        dao.update(
            place.copy(
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun visit(id: Int) {
        dao.visit(
            id = id,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun unvisit(id: Int) {
        dao.unvisit(id)
    }

    suspend fun hardDelete(place: Place) {
        dao.hardDelete(place)
    }


    suspend fun toggleView(current: String) {
        val newValue = if (current == "list") "grid" else "list"
        preferences.setViewMode(newValue)
    }
}