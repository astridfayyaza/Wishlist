package com.astrid0049.wishlist.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astrid0049.wishlist.WishlistApplication
import com.astrid0049.wishlist.data.Place
import com.astrid0049.wishlist.data.PlaceRepository
import com.astrid0049.wishlist.nav.KEY_ID_PLACE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaceDetailViewModel(
    private val repository: PlaceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Int? = savedStateHandle[KEY_ID_PLACE]

    val isEditMode: Boolean = id != null

    private val _currentPlace = MutableStateFlow<Place?>(null)
    val currentPlace: StateFlow<Place?> = _currentPlace

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _isSaved = MutableStateFlow(false)
    private val _actionMessage = MutableStateFlow("")
    val actionMessage: StateFlow<String> = _actionMessage
    val isSaved: StateFlow<Boolean> = _isSaved

    init {
        if (id != null) {
            viewModelScope.launch {
                val place = repository.getById(id).first()
                _currentPlace.value = place

                if (place != null) {
                    _name.value = place.name
                    _location.value = place.location.orEmpty()
                    _category.value = place.category
                    _notes.value = place.notes.orEmpty()
                }
            }
        }
    }

    fun updateName(value: String) {
        _name.value = value
    }

    fun updateLocation(value: String) {
        _location.value = value
    }

    fun updateCategory(value: String) {
        _category.value = value
    }

    fun updateNotes(value: String) {
        _notes.value = value
    }

    fun isInputValid(): Boolean {
        return _name.value.isNotBlank() && _category.value.isNotBlank()
    }

    fun save() {
        if (!isInputValid()) return

        viewModelScope.launch {
            if (isEditMode) {
                val oldPlace = _currentPlace.value ?: return@launch

                repository.updatePlace(
                    oldPlace.copy(
                        name = _name.value.trim(),
                        location = _location.value.trim().ifBlank { null },
                        category = _category.value,
                        notes = _notes.value.trim().ifBlank { null }
                    )
                )
            } else {
                repository.addPlace(
                    name = _name.value.trim(),
                    location = _location.value.trim().ifBlank { null },
                    category = _category.value,
                    notes = _notes.value.trim().ifBlank { null }
                )
            }

            _actionMessage.value = if (isEditMode) {
                "Saved. ✦"
            } else {
                "'${_name.value.trim()}' pinned. ✦"
            }

            _isSaved.value = true
        }
    }

    fun markVisited() {
        val placeId = id ?: return

        viewModelScope.launch {
            repository.visit(placeId)

            _actionMessage.value = "'${_name.value.trim()}' visited. ✨"
            _isSaved.value = true
        }
    }

    fun deletePlace() {
        val place = _currentPlace.value ?: return

        viewModelScope.launch {
            repository.hardDelete(place)

            _actionMessage.value = "'${place.name}' deleted."
            _isSaved.value = true
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WishlistApplication
                val savedStateHandle = createSavedStateHandle()

                PlaceDetailViewModel(
                    repository = app.repository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}