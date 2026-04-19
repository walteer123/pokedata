package com.pokedata.core.testing

import androidx.paging.PagingData
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePokemonRepository : PokemonRepositoryInterface {

    private val pokemonList = mutableListOf<PokemonListItem>()
    private val pokemonDetails = mutableMapOf<Int, PokemonDetail>()

    fun addPokemon(vararg pokemon: PokemonListItem) {
        pokemonList.addAll(pokemon)
    }

    fun addDetail(detail: PokemonDetail) {
        pokemonDetails[detail.id] = detail
    }

    override fun getPokemonList(typeFilter: String?): Flow<PagingData<PokemonListItem>> {
        val filteredList = if (typeFilter != null) {
            pokemonList.filter { it.types.contains(typeFilter.lowercase()) }
        } else {
            pokemonList
        }
        return flow {
            emit(PagingData.from(filteredList))
        }
    }

    override suspend fun fetchInitialPokemonList() {
        // Fake implementation - não faz nada
    }

    override suspend fun refreshPokemonList() {
        // Fake implementation - não faz nada
    }

    override suspend fun fetchAndCachePokemonList(offset: Int, limit: Int) {
        // Fake implementation - não faz nada
    }

    override suspend fun getPokemonDetail(id: Int): PokemonDetail {
        return pokemonDetails[id] ?: throw IllegalStateException("Pokemon $id not found")
    }

    override suspend fun toggleFavorite(id: Int) {
        val index = pokemonList.indexOfFirst { it.id == id }
        if (index != -1) {
            val pokemon = pokemonList[index]
            pokemonList[index] = pokemon.copy(isFavorite = !pokemon.isFavorite)
        }
    }

    override suspend fun searchPokemon(query: String): List<PokemonListItem> {
        return pokemonList.filter {
            it.name.contains(query, ignoreCase = true) || it.id.toString() == query
        }
    }

    override fun getFavorites(): Flow<List<PokemonListItem>> {
        return flow {
            emit(pokemonList.filter { it.isFavorite })
        }
    }
}
