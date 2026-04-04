package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: PokemonRepositoryInterface
) {
    operator fun invoke(): Flow<List<PokemonListItem>> {
        return repository.getFavorites()
    }
}
