package com.astrid0049.wishlist.ui.wishlist

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.astrid0049.wishlist.R
import com.astrid0049.wishlist.data.Place
import com.astrid0049.wishlist.nav.Screen
import com.astrid0049.wishlist.ui.theme.WishlistTheme
import com.astrid0049.wishlist.util.getCategoryLabelRes
import com.astrid0049.wishlist.util.pineDays
import com.astrid0049.wishlist.util.pineLabelRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavHostController,
    viewModel: WishlistViewModel = viewModel(factory = WishlistViewModel.Factory)
) {
    val places by viewModel.places.collectAsState(initial = emptyList())
    val viewMode by viewModel.viewMode.collectAsState(initial = "list")

    WishlistScreenContent(
        places = places,
        viewMode = viewMode,
        onToggleView = { viewModel.toggleView(viewMode) },
        onAddClick = { navController.navigate(Screen.PlaceAdd.route) },
        onPlaceClick = { place -> navController.navigate(Screen.PlaceEdit.withId(place.id)) },
        onVisitedClick = { navController.navigate(Screen.Visited.route) },
        onAboutClick = { navController.navigate(Screen.About.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreenContent(
    places: List<Place>,
    viewMode: String,
    onToggleView: () -> Unit,
    onAddClick: () -> Unit,
    onPlaceClick: (Place) -> Unit,
    onVisitedClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.wishlist_title),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = onToggleView
                    ) {
                        Icon(
                            imageVector = if (viewMode == "list") Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
                            contentDescription = if (viewMode == "list") stringResource(R.string.show_grid) else stringResource(R.string.show_list)
                        )
                    }

                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.cd_menu)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_visited)) },
                            onClick = {
                                menuExpanded = false
                                onVisitedClick()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_about)) },
                            onClick = {
                                menuExpanded = false
                                onAboutClick()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_place)
                    )
                },
                text = {
                    Text(stringResource(R.string.pin_a_place))
                },
                elevation = FloatingActionButtonDefaults.elevation()
            )
        }
    ) { innerPadding ->
        if (places.isEmpty()) {
            EmptyWishlist(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            if (viewMode == "list") {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(places) { place ->
                        PlaceCard(
                            place = place,
                            onClick = {
                                onPlaceClick(place)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                WishlistGrid(
                    places = places,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    onPlaceClick = { place ->
                        onPlaceClick(place)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyWishlist(
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
            text = stringResource(R.string.wishlist_empty_primary),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.wishlist_empty_secondary),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PlaceCard(
    place: Place,
    onClick: () -> Unit
) {
    val days = pineDays(place)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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

                if (!place.notes.isNullOrBlank()) {
                    Text(
                        text = place.notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.pine_days, days),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(pineLabelRes(days)),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WishlistGrid(
    places: List<Place>,
    modifier: Modifier = Modifier,
    onPlaceClick: (Place) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(places) { place ->
            GridPlaceCard(
                place = place,
                onClick = {
                    onPlaceClick(place)
                }
            )
        }
    }
}

@Composable
private fun GridPlaceCard(
    place: Place,
    onClick: () -> Unit
) {
    val days = pineDays(place)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.pine_days, days),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (!place.location.isNullOrBlank()) {
                Text(
                    text = place.location,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }

            Text(
                text = stringResource(getCategoryLabelRes(place.category)),
                style = MaterialTheme.typography.labelSmall
            )

            if (!place.notes.isNullOrBlank()) {
                Text(
                    text = place.notes,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            Text(
                text = stringResource(pineLabelRes(days)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistTheme {
        WishlistScreenContent(
            places = listOf(
                Place(
                    id = 1,
                    name = "Mount Fuji",
                    location = "Japan",
                    category = "Nature",
                    notes = "Must see in winter",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                ),
                Place(
                    id = 2,
                    name = "Colosseum",
                    location = "Rome, Italy",
                    category = "Culture",
                    dateAdded = System.currentTimeMillis() - 86400000 * 5,
                    lastUpdated = System.currentTimeMillis()
                )
            ),
            viewMode = "list",
            onToggleView = {},
            onAddClick = {},
            onPlaceClick = {},
            onVisitedClick = {},
            onAboutClick = {}
        )
    }
}