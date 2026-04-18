package com.pokedata.core.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("route")
sealed class Route {

    @Serializable
    @SerialName("pokemon_list")
    data object PokemonList : Route()

    @Serializable
    @SerialName("pokemon_detail")
    data class PokemonDetail(val pokemonId: Int) : Route()

    @Serializable
    @SerialName("search")
    data object Search : Route()

    @Serializable
    @SerialName("favorites")
    data object Favorites : Route()
}
