package com.pokedata.core.testing

import com.pokedata.core.data.model.PokemonAbility
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.model.PokemonType

object TestData {

    val bulbasaurListItem = PokemonListItem(
        id = 1,
        name = "bulbasaur",
        spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
        isFavorite = false
    )

    val charmanderListItem = PokemonListItem(
        id = 4,
        name = "charmander",
        spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
        isFavorite = true
    )

    val pikachuListItem = PokemonListItem(
        id = 25,
        name = "pikachu",
        spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
        isFavorite = false
    )

    val bulbasaurDetail = PokemonDetail(
        id = 1,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
        artworkUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
        description = "A strange seed was planted on its back at birth.",
        genus = "Seed Pokemon",
        types = listOf(
            PokemonType(name = "grass", slot = 1),
            PokemonType(name = "poison", slot = 2)
        ),
        abilities = listOf(
            PokemonAbility(name = "overgrow", isHidden = false),
            PokemonAbility(name = "chlorophyll", isHidden = true)
        ),
        stats = mapOf(
            "hp" to 45,
            "attack" to 49,
            "defense" to 49,
            "special-attack" to 65,
            "special-defense" to 65,
            "speed" to 45
        ),
        isFavorite = false
    )

    val charmanderDetail = PokemonDetail(
        id = 4,
        name = "charmander",
        height = 6,
        weight = 85,
        spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
        artworkUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
        description = "The flame that burns at the tip of its tail is an indication of its emotions.",
        genus = "Lizard Pokemon",
        types = listOf(
            PokemonType(name = "fire", slot = 1)
        ),
        abilities = listOf(
            PokemonAbility(name = "blaze", isHidden = false),
            PokemonAbility(name = "solar-power", isHidden = true)
        ),
        stats = mapOf(
            "hp" to 39,
            "attack" to 52,
            "defense" to 43,
            "special-attack" to 60,
            "special-defense" to 50,
            "speed" to 65
        ),
        isFavorite = true
    )

    fun samplePokemonList(): List<PokemonListItem> = listOf(
        bulbasaurListItem,
        charmanderListItem,
        pikachuListItem
    )

    fun samplePokemonDetails(): List<PokemonDetail> = listOf(
        bulbasaurDetail,
        charmanderDetail
    )
}
