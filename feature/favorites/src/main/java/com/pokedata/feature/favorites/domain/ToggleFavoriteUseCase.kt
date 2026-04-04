package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.repository.PokemonRepository

class ToggleFavoriteUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        repository.toggleFavorite(pokemonId)
    }
}
