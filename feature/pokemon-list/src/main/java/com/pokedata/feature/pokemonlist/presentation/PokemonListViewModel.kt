package com.pokedata.feature.pokemonlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class PokemonListUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTypeFilter: String? = null
)

val PokemonTypes = listOf(
    "normal", "fire", "water", "electric", "grass", "ice",
    "fighting", "poison", "ground", "flying", "psychic", "bug",
    "rock", "ghost", "dragon", "dark", "steel", "fairy"
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PokemonListViewModel(
    private val repository: PokemonRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private val _typeFilter = MutableStateFlow<String?>(null)

    val pokemonList: Flow<PagingData<PokemonListItem>> = _typeFilter
        .flatMapLatest { filter ->
            repository.getPokemonList(typeFilter = filter)
        }
        .catch { e ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to load Pokemon"
            )
        }
        .cachedIn(viewModelScope)

    init {
        loadPokemon()
    }

    private fun loadPokemon() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.fetchInitialPokemonList()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load Pokemon"
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            try {
                repository.fetchInitialPokemonList()
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Failed to refresh"
                )
            }
        }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setTypeFilter(type: String?) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = type)
        _typeFilter.value = type
    }
}
