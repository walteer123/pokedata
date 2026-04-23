package com.pokedata.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<PokemonListItem> = emptyList(),
    val isLoading: Boolean = true,
    val removingIds: Set<Int> = emptySet()
)

class FavoritesViewModel(
    private val repository: PokemonRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        repository.getFavorites()
            .onEach { favorites ->
                val currentIds = favorites.map { it.id }.toSet()
                _uiState.value = _uiState.value.copy(
                    favorites = favorites,
                    isLoading = false,
                    removingIds = _uiState.value.removingIds.intersect(currentIds)
                )
            }
            .launchIn(viewModelScope)
    }

    fun removeFavorite(pokemonId: Int) {
        if (_uiState.value.removingIds.contains(pokemonId)) return

        _uiState.value = _uiState.value.copy(
            removingIds = _uiState.value.removingIds + pokemonId
        )

        viewModelScope.launch {
            delay(300)
            repository.toggleFavorite(pokemonId)
        }
    }
}
