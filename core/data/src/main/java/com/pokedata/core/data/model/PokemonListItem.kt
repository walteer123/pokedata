package com.pokedata.core.data.model

data class PokemonListItem(
    val id: Int,
    val name: String,
    val spriteUrl: String?,
    val isFavorite: Boolean,
    val types: List<String> = emptyList()
)
