package com.astrid0049.wishlist

import android.app.Application
import com.astrid0049.wishlist.data.PlaceDatabase
import com.astrid0049.wishlist.data.PlaceRepository
import com.astrid0049.wishlist.data.UserPreferencesRepository
import com.astrid0049.wishlist.data.dataStore

class WishlistApplication: Application() {

        lateinit var repository: PlaceRepository
            private set

        override fun onCreate() {
            super.onCreate()

            val database = PlaceDatabase.getDatabase(this)
            val preferences = UserPreferencesRepository(dataStore)

            repository = PlaceRepository(
                dao = database.placeDao(),
                preferences = preferences
            )
        }
    }