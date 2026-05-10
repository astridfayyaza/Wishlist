package com.astrid0049.wishlist.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astrid0049.wishlist.WishlistApplication
import com.astrid0049.wishlist.data.PlaceRepository
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    val places = repository.activePlaces()
    val viewMode = repository.viewMode

    fun toggleView(current: String) {
        viewModelScope.launch {
            repository.toggleView(current)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WishlistApplication
                WishlistViewModel(app.repository)
            }
        }
    }
}