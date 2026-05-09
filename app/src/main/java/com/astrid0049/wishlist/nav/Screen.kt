package com.astrid0049.wishlist.nav
const val KEY_ID_PLACE = "idPlace"

sealed class Screen(val route: String) {
    data object Wishlist : Screen("mainScreen")

    data object PlaceAdd : Screen("detailScreen")

    data object PlaceEdit : Screen("detailScreen/{$KEY_ID_PLACE}") {
        fun withId(id: Int) = "detailScreen/$id"
    }

    data object Visited : Screen("visitedScreen")

    data object About : Screen("aboutScreen")
}