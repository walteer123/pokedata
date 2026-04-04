package com.pokedata.core.navigation

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object PokemonList : Route()

    @Serializable
    data class PokemonDetail(val pokemonId: Int) : Route()

    @Serializable
    data object Search : Route()

    @Serializable
    data object Favorites : Route()
}
