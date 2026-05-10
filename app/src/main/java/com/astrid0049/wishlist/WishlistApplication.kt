package com.astrid0049.wishlist

import android.app.Application
import com.astrid0049.wishlist.data.PlaceDatabase
import com.astrid0049.wishlist.data.PlaceRepository
import com.astrid0049.wishlist.data.UserPreferencesRepository
import com.astrid0049.wishlist.data.dataStore
import com.astrid0049.wishlist.data.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

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

            seedDatabase(database)
        }

        private fun seedDatabase(database: PlaceDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.placeDao()

            if (dao.countAllPlaces() == 0) {
                val now = System.currentTimeMillis()
                val day = 1000L * 60 * 60 * 24

                fun dateMillis(year: Int, month: Int, day: Int): Long {
                    return LocalDate.of(year, month, day)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }

                // -----------------------
                // Wishlist / pinned places
                // -----------------------

                dao.insert(
                    Place(
                        name = "Taman Hutan Raya",
                        location = "Bandung",
                        category = "Nature",
                        notes = "A calm green escape for someday.",
                        dateAdded = now - (42 * day),
                        lastUpdated = now - (42 * day),
                        visitedAt = null
                    )
                )

                dao.insert(
                    Place(
                        name = "Dago Dream Park",
                        location = "Bandung",
                        category = "Nature",
                        notes = "A playful little trip idea.",
                        dateAdded = now - (35 * day),
                        lastUpdated = now - (35 * day),
                        visitedAt = null
                    )
                )

                dao.insert(
                    Place(
                        name = "Le Von Boulangerie",
                        location = "Bandung",
                        category = "Food",
                        notes = "Bakery date, obviously.",
                        dateAdded = now - (28 * day),
                        lastUpdated = now - (28 * day),
                        visitedAt = null
                    )
                )

                dao.insert(
                    Place(
                        name = "Mercusuar Cafe Resto",
                        location = "Bandung",
                        category = "Food",
                        notes = "For the view and the vibe.",
                        dateAdded = now - (24 * day),
                        lastUpdated = now - (24 * day),
                        visitedAt = null
                    )
                )

                dao.insert(
                    Place(
                        name = "Secret Place",
                        location = "Mekarwangi, Bandung",
                        category = "Hidden Gem",
                        notes = "It's a secret.",
                        dateAdded = now - (18 * day),
                        lastUpdated = now - (18 * day),
                        visitedAt = null
                    )
                )

                dao.insert(
                    Place(
                        name = "Japan",
                        location = "Hokkaido, Japan",
                        category = "Culture",
                        notes = "The big someday dream.",
                        dateAdded = now - (90 * day),
                        lastUpdated = now - (90 * day),
                        visitedAt = null
                    )
                )

                // -----------------------
                // Visited places
                // -----------------------

                dao.insert(
                    Place(
                        name = "Rolun Cafe",
                        location = "Bojongsoang, Bandung",
                        category = "Food",
                        notes = "Visited together.",
                        dateAdded = dateMillis(2026, 4, 1),
                        lastUpdated = dateMillis(2026, 4, 1),
                        visitedAt = dateMillis(2026, 4, 28)
                    )
                )

                dao.insert(
                    Place(
                        name = "Summarecon Mall Bandung",
                        location = "Bandung",
                        category = "City",
                        notes = "Mall day.",
                        dateAdded = dateMillis(2025, 12, 20),
                        lastUpdated = dateMillis(2025, 12, 20),
                        visitedAt = dateMillis(2026, 1, 6)
                    )
                )

                dao.insert(
                    Place(
                        name = "Tenth Avenue Mall Bandung",
                        location = "Bandung",
                        category = "City",
                        notes = "Visited on the same city trip.",
                        dateAdded = dateMillis(2025, 12, 1),
                        lastUpdated = dateMillis(2025, 12, 1),
                        visitedAt = dateMillis(2025, 12, 28)
                    )
                )

                dao.insert(
                    Place(
                        name = "ITB Bandung",
                        location = "Bandung",
                        category = "Culture",
                        notes = "Campus walk.",
                        dateAdded = dateMillis(2025, 12, 1),
                        lastUpdated = dateMillis(2025, 12, 1),
                        visitedAt = dateMillis(2025, 12, 28)
                    )
                )

                dao.insert(
                    Place(
                        name = "D.Dough Cafe",
                        location = "Bandung",
                        category = "Food",
                        notes = "Cafe stop.",
                        dateAdded = dateMillis(2025, 12, 1),
                        lastUpdated = dateMillis(2025, 12, 1),
                        visitedAt = dateMillis(2025, 12, 28)
                    )
                )

                dao.insert(
                    Place(
                        name = "Saho Bakery",
                        location = "Bandung",
                        category = "Food",
                        notes = "Bakery visit.",
                        dateAdded = dateMillis(2025, 11, 10),
                        lastUpdated = dateMillis(2025, 11, 10),
                        visitedAt = dateMillis(2025, 11, 29)
                    )
                )

                dao.insert(
                    Place(
                        name = "DevFest Bandung",
                        location = "Bandung",
                        category = "Culture",
                        notes = "Tech event day.",
                        dateAdded = dateMillis(2025, 11, 1),
                        lastUpdated = dateMillis(2025, 11, 1),
                        visitedAt = dateMillis(2025, 11, 29)
                    )
                )

                dao.insert(
                    Place(
                        name = "Braga",
                        location = "Bandung",
                        category = "City",
                        notes = "Classic Bandung walk.",
                        dateAdded = dateMillis(2025, 10, 1),
                        lastUpdated = dateMillis(2025, 10, 1),
                        visitedAt = dateMillis(2025, 10, 28)
                    )
                )
            }
        }
    }
    }