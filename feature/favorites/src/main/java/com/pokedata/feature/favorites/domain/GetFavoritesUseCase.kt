package com.pokedata.feature.favorites.domain

import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<PokemonListItem>> {
        return repository.getFavorites()
    }
}
