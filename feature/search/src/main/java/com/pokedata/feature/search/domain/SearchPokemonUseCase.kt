package com.pokedata.feature.search.domain

import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchPokemonUseCase(
    private val repository: PokemonRepositoryInterface
) {
    operator fun invoke(query: String): Flow<List<PokemonListItem>> {
        return flow {
            if (query.isBlank()) {
                emit(emptyList())
            } else {
                emit(repository.searchPokemon(query))
            }
        }
    }
}
