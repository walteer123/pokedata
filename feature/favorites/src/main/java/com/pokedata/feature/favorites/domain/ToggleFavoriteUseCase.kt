package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.repository.PokemonRepositoryInterface

class ToggleFavoriteUseCase(
    private val repository: PokemonRepositoryInterface
) {
    suspend operator fun invoke(pokemonId: Int) {
        repository.toggleFavorite(pokemonId)
    }
}
