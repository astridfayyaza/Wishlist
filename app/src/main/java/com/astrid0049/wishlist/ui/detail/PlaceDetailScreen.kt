package com.astrid0049.wishlist.ui.detail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.astrid0049.wishlist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.astrid0049.wishlist.ui.theme.WishlistTheme

@Composable
fun PlaceDetailScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    viewModel: PlaceDetailViewModel = viewModel(factory = PlaceDetailViewModel.Factory)
){
    val name by viewModel.name.collectAsState()
    val location by viewModel.location.collectAsState()
    val category by viewModel.category.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val showErrors by viewModel.showErrors.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    val savedMsg = stringResource(R.string.snackbar_saved)
    val pinnedMsg = stringResource(R.string.snackbar_pinned, name)
    val visitedMsg = stringResource(R.string.snackbar_visited, name)
    val deletedMsg = stringResource(R.string.snackbar_deleted, name)
    val undoMsg = stringResource(R.string.undo)

    LaunchedEffect(isSaved) {
        if (isSaved) {
            navController.popBackStack()

            snackbarScope.launch {
                val message = when (actionMessage) {
                    "saved" -> savedMsg
                    "pinned" -> pinnedMsg
                    "visited" -> visitedMsg
                    "deleted" -> deletedMsg
                    else -> actionMessage
                }
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = if (actionMessage == "visited") undoMsg else null
                )

                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    viewModel.undoVisit()
                }
            }
        }
    }

    PlaceDetailScreenContent(
        isEditMode = viewModel.isEditMode,
        name = name,
        location = location,
        category = category,
        notes = notes,
        showErrors = showErrors,
        onNameChange = viewModel::updateName,
        onLocationChange = viewModel::updateLocation,
        onCategoryChange = viewModel::updateCategory,
        onNotesChange = viewModel::updateNotes,
        onSave = {
            viewModel.save()
            return@PlaceDetailScreenContent viewModel.isInputValid()
        },
        onBackClick = { navController.popBackStack() },
        onMarkVisited = viewModel::markVisited,
        onDeletePlace = viewModel::deletePlace
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreenContent(
    isEditMode: Boolean,
    name: String,
    location: String,
    category: String,
    notes: String,
    showErrors: Boolean,
    onNameChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Boolean,
    onBackClick: () -> Unit,
    onMarkVisited: () -> Unit,
    onDeletePlace: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val categories = listOf(
        "Food" to R.string.cat_food,
        "Nature" to R.string.cat_nature,
        "City" to R.string.cat_city,
        "Culture" to R.string.cat_culture,
        "Hidden Gem" to R.string.cat_hidden_gem
    )

    val validationToastMsg = stringResource(R.string.validation_toast)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) stringResource(R.string.place_edit_title) else stringResource(R.string.place_add_title),
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
                },
                actions = {
                    if (isEditMode) {
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
                                text = { Text(stringResource(R.string.mark_visited)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onMarkVisited()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.delete),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog.value = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.field_where)) },
                isError = showErrors && name.isBlank(),
                supportingText = {
                    if (showErrors && name.isBlank()) {
                        Text(stringResource(R.string.validation_name_empty))
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text(stringResource(R.string.field_location)) },
                placeholder = { Text(stringResource(R.string.field_location_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_category)) },
                    isError = showErrors && category.isBlank(),
                    supportingText = {
                        if (showErrors && category.isBlank()) {
                            Text(stringResource(R.string.validation_category_empty))
                        }
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {
                    categories.forEach { (value, labelRes) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(labelRes)) },
                            onClick = {
                                onCategoryChange(value)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text(stringResource(R.string.field_notes)) },
                placeholder = {
                    Text(stringResource(R.string.field_notes_placeholder))
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val isValid = onSave()
                    if (!isValid) {
                        Toast.makeText(
                            context,
                            validationToastMsg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditMode) {
                        stringResource(R.string.save_changes)
                    } else {
                        stringResource(R.string.pin_it)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog.value = false
            },
            title = {
                Text(stringResource(R.string.delete_place_title, name))
            },
            text = {
                Text(stringResource(R.string.delete_place_body))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        onDeletePlace()
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
                        showDeleteDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.keep_it))
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Add Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PlaceAddScreenPreview() {
    WishlistTheme {
        PlaceDetailScreenContent(
            isEditMode = false,
            name = "",
            location = "",
            category = "",
            notes = "",
            showErrors = false,
            onNameChange = {},
            onLocationChange = {},
            onCategoryChange = {},
            onNotesChange = {},
            onSave = { true },
            onBackClick = {},
            onMarkVisited = {},
            onDeletePlace = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PlaceEditScreenPreview() {
    WishlistTheme {
        PlaceDetailScreenContent(
            isEditMode = true,
            name = "Mount Fuji",
            location = "Japan",
            category = "Nature",
            notes = "Beautiful in winter",
            showErrors = false,
            onNameChange = {},
            onLocationChange = {},
            onCategoryChange = {},
            onNotesChange = {},
            onSave = { true },
            onBackClick = {},
            onMarkVisited = {},
            onDeletePlace = {}
        )
    }
}
