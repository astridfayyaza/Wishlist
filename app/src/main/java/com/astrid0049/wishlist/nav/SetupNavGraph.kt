package com.astrid0049.wishlist.nav

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.CoroutineScope

@Composable
fun SetupNavGraph(
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Wishlist.route,
        modifier = modifier
    ) {
        composable(route = Screen.Wishlist.route) {
            WishlistScreen(navController)
        }

        composable(route = Screen.PlaceAdd.route) {
            PlaceDetailScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope
            )
        }

        composable(
            route = Screen.PlaceEdit.route,
            arguments = listOf(
                navArgument(KEY_ID_PLACE) {
                    type = NavType.IntType
                }
            )
        ) {
            PlaceDetailScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope
            )
        }

        composable(route = Screen.Visited.route) {
            VisitedScreen(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }

        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
    }
}