package com.pokedata.feature.pokemondetail.domain

import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.repository.PokemonRepository

class GetPokemonDetailUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int): PokemonDetail {
        return repository.getPokemonDetail(pokemonId)
    }
}
