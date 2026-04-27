package com.pokedata.feature.pokemondetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PokemonDetailUiState(
    val isLoading: Boolean = true,
    val pokemon: PokemonDetail? = null,
    val error: String? = null
)

class PokemonDetailViewModel(
    private val repository: PokemonRepositoryInterface,
    private val pokemonId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        loadPokemonDetail()
    }

    private fun loadPokemonDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val detail = repository.getPokemonDetail(pokemonId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pokemon = detail
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load Pokemon details"
                )
            }
        }
    }

    fun reload() {
        loadPokemonDetail()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
            val current = _uiState.value.pokemon
            if (current != null) {
                _uiState.value = _uiState.value.copy(
                    pokemon = current.copy(isFavorite = !current.isFavorite)
                )
            }
        }
    }
}
