package com.pokedata.feature.pokemonlist.domain

import androidx.paging.PagingData
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetPokemonListUseCase(
    private val repository: PokemonRepositoryInterface
) {
    operator fun invoke(): Flow<PagingData<PokemonListItem>> {
        return repository.getPokemonList()
    }
}
