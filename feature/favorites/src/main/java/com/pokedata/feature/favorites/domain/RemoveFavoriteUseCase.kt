package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.repository.PokemonRepository

class RemoveFavoriteUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        val repository = repository
        repository.toggleFavorite(pokemonId)
    }
}
