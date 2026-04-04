package com.pokedata.feature.search.domain

import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchPokemonUseCase(
    private val repository: PokemonRepository
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
