package com.astrid0049.wishlist.ui.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astrid0049.wishlist.WishlistApplication
import com.astrid0049.wishlist.data.Place
import com.astrid0049.wishlist.data.PlaceRepository
import kotlinx.coroutines.launch

class VisitedViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    val visitedPlaces = repository.visitedPlaces()

    fun restorePlace(id: Int) {
        viewModelScope.launch {
            repository.unvisit(id)
        }
    }

    fun deletePlace(place: Place) {
        viewModelScope.launch {
            repository.hardDelete(place)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WishlistApplication
                VisitedViewModel(app.repository)
            }
        }
    }
}