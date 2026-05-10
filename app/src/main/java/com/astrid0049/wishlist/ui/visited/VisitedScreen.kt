package com.astrid0049.wishlist.ui.visited

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.astrid0049.wishlist.R
import com.astrid0049.wishlist.data.Place
import com.astrid0049.wishlist.util.getCategoryLabelRes
import com.astrid0049.wishlist.util.relativeDate
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.astrid0049.wishlist.ui.theme.WishlistTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: VisitedViewModel = viewModel(factory = VisitedViewModel.Factory)
) {
    val places by viewModel.visitedPlaces.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    VisitedScreenContent(
        places = places,
        onBackClick = { navController.popBackStack() },
        onRestorePlace = { placeId, restoredMessage ->
            viewModel.restorePlace(placeId)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(restoredMessage)
            }
        },
        onDeletePlace = { place, deletedMessage ->
            viewModel.deletePlace(place)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(deletedMessage)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedScreenContent(
    places: List<Place>,
    onBackClick: () -> Unit,
    onRestorePlace: (Int, String) -> Unit,
    onDeletePlace: (Place, String) -> Unit
) {
    var placeToDelete by remember { mutableStateOf<Place?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.visited_title),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (places.isEmpty()) {
            EmptyVisited(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.visited_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(places) { place ->
                    val restoredMessage = stringResource(R.string.snackbar_restored, place.name)
                    VisitedCard(
                        place = place,
                        onRestore = {
                            onRestorePlace(place.id, restoredMessage)
                        },
                        onDelete = {
                            placeToDelete = place
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    val currentPlaceToDelete = placeToDelete
    if (currentPlaceToDelete != null) {
        val deleteTitle = stringResource(R.string.delete_place_title, currentPlaceToDelete.name)
        val deletedMessage = stringResource(R.string.snackbar_deleted, currentPlaceToDelete.name)
        AlertDialog(
            onDismissRequest = {
                placeToDelete = null
            },
            title = {
                Text(deleteTitle)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.kuromi_cry),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                    )
                    Text(stringResource(R.string.delete_visited_body))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePlace(currentPlaceToDelete, deletedMessage)
                        placeToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        placeToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.keep_it))
                }
            }
        )
    }
}

@Composable
private fun EmptyVisited(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.kuromi_back),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
        )

        Text(
            text = stringResource(R.string.visited_empty_primary),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.visited_empty_secondary),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun VisitedCard(
    place: Place,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val (resId, arg) = place.visitedAt?.let {
                    relativeDate(it)
                } ?: (R.string.mark_visited to null)

                val visitedText = if (arg != null) {
                    stringResource(resId, arg)
                } else {
                    stringResource(resId)
                }

                Text(
                    text = stringResource(R.string.visited_relative, visitedText),
                    style = MaterialTheme.typography.bodySmall
                )

                val dot = stringResource(R.string.dot_separator)
                val categoryLabel = stringResource(getCategoryLabelRes(place.category))
                val subtitle = remember(place.location, categoryLabel, dot) {
                    buildString {
                        if (!place.location.isNullOrBlank()) {
                            append(place.location)
                            append(dot)
                        }
                        append(categoryLabel)
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                IconButton(
                    onClick = onRestore
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.restore)
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = stringResource(R.string.delete_forever),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun VisitedScreenPreview() {
    val now = System.currentTimeMillis()
    WishlistTheme {
        VisitedScreenContent(
            places = listOf(
                Place(
                    id = 1,
                    name = "Eiffel Tower",
                    location = "Paris",
                    category = "Culture",
                    visitedAt = now - 86400000 * 10,
                    dateAdded = now - 86400000 * 20,
                    lastUpdated = now
                ),
                Place(
                    id = 2,
                    name = "Great Wall",
                    location = "China",
                    category = "Culture",
                    visitedAt = now - 86400000 * 30,
                    dateAdded = now - 86400000 * 40,
                    lastUpdated = now
                )
            ),
            onBackClick = {},
            onRestorePlace = { _, _ -> },
            onDeletePlace = { _, _ -> }
        )
    }
}
