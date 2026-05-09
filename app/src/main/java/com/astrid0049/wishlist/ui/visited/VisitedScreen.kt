package com.astrid0049.wishlist.ui.visited

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.astrid0049.wishlist.data.Place
import com.astrid0049.wishlist.util.relativeDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: VisitedViewModel = viewModel(factory = VisitedViewModel.Factory)
) {
    val places by viewModel.visitedPlaces.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var placeToDelete by remember { mutableStateOf<Place?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Visited",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                        text = "Places we made happen. ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(places) { place ->
                    VisitedCard(
                        place = place,
                        onRestore = {
                            viewModel.restorePlace(place.id)

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "'${place.name}' is back on the wishlist."
                                )
                            }
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

    if (placeToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                placeToDelete = null
            },
            title = {
                Text("Delete '${placeToDelete?.name}'?")
            },
            text = {
                Text("Removing this from your library. No coming back.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        placeToDelete?.let { place ->
                            viewModel.deletePlace(place)

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "'${place.name}' deleted."
                                )
                            }
                        }
                        placeToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
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
                    Text("Keep it")
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
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Nowhere yet.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Make a memory. Then come back.",
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

                val visitedText = place.visitedAt?.let {
                    "Visited ${relativeDate(it)}"
                } ?: "Visited"

                Text(
                    text = visitedText,
                    style = MaterialTheme.typography.bodySmall
                )

                val subtitle = buildString {
                    if (!place.location.isNullOrBlank()) {
                        append(place.location)
                        append(" · ")
                    }
                    append(place.category)
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
                        contentDescription = "Restore"
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete forever",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}