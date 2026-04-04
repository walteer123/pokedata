package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.repository.PokemonRepositoryInterface

class RemoveFavoriteUseCase(
    private val repository: PokemonRepositoryInterface
) {
    suspend operator fun invoke(pokemonId: Int) {
        val repository = repository
        repository.toggleFavorite(pokemonId)
    }
}
