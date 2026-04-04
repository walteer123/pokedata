package com.pokedata.core.data.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val spriteUrl: String?,
    val artworkUrl: String?,
    val description: String?,
    val genus: String?,
    val types: List<PokemonType>,
    val abilities: List<PokemonAbility>,
    val stats: Map<String, Int>,
    val isFavorite: Boolean
)

data class PokemonType(
    val name: String,
    val slot: Int
)

data class PokemonAbility(
    val name: String,
    val isHidden: Boolean
)
