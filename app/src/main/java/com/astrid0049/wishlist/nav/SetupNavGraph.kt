package com.astrid0049.wishlist.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.astrid0049.wishlist.ui.about.AboutScreen
import com.astrid0049.wishlist.ui.detail.PlaceDetailScreen
import com.astrid0049.wishlist.ui.visited.VisitedScreen
import com.astrid0049.wishlist.ui.wishlist.WishlistScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Wishlist.route
    ) {
        composable(route = Screen.Wishlist.route) {
            WishlistScreen(navController)
        }

        composable(route = Screen.PlaceAdd.route) {
            PlaceDetailScreen(navController = navController)
        }

        composable(
            route = Screen.PlaceEdit.route,
            arguments = listOf(
                navArgument(KEY_ID_PLACE) {
                    type = NavType.IntType
                }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(KEY_ID_PLACE)
            PlaceDetailScreen(
                navController = navController,
                id = id
            )
        }

        composable(route = Screen.Visited.route) {
            VisitedScreen(navController)
        }

        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
    }
}