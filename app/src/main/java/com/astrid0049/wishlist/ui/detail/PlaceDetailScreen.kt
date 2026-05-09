package com.example.itsuka.ui.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun PlaceDetailScreen(
    navController: NavHostController,
    id: Int? = null
) {
    if (id == null) {
        Text(text = "Add Place Screen")
    } else {
        Text(text = "Edit Place Screen, id = $id")
    }
}