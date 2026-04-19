package com.pokedata.core.data.repository

import androidx.paging.PagingData
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonListItem
import kotlinx.coroutines.flow.Flow

interface PokemonRepositoryInterface {
    fun getPokemonList(typeFilter: String? = null): Flow<PagingData<PokemonListItem>>
    suspend fun fetchInitialPokemonList()
    suspend fun refreshPokemonList()
    suspend fun fetchAndCachePokemonList(offset: Int, limit: Int)
    suspend fun getPokemonDetail(id: Int): PokemonDetail
    suspend fun toggleFavorite(id: Int)
    suspend fun searchPokemon(query: String): List<PokemonListItem>
    fun getFavorites(): Flow<List<PokemonListItem>>
}
