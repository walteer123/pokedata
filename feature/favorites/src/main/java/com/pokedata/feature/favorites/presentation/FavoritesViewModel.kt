package com.pokedata.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<PokemonListItem> = emptyList(),
    val isLoading: Boolean = true
)

class FavoritesViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        repository.getFavorites()
            .onEach { favorites ->
                _uiState.value = _uiState.value.copy(
                    favorites = favorites,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun removeFavorite(pokemonId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
        }
    }
}
