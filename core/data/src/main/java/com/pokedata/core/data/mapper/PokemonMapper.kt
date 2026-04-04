package com.pokedata.core.data.mapper

import com.pokedata.core.data.local.entity.AbilityEntity
import com.pokedata.core.data.local.entity.BaseStatsEntity
import com.pokedata.core.data.local.entity.PokemonEntity
import com.pokedata.core.data.local.entity.PokemonTypeEntity
import com.pokedata.core.data.remote.dto.PokemonDetailResponse
import com.pokedata.core.data.remote.dto.PokemonSummary

fun PokemonSummary.toPokemonEntity(
    id: Int,
    spriteUrl: String? = null,
    artworkUrl: String? = null
): PokemonEntity {
    return PokemonEntity(
        id = id,
        name = name,
        height = 0,
        weight = 0,
        spriteUrl = spriteUrl,
        artworkUrl = artworkUrl,
        description = null,
        genus = null
    )
}

fun PokemonDetailResponse.toPokemonEntity(
    description: String? = null,
    genus: String? = null,
    isFavorite: Boolean = false,
    lastUpdated: Long = System.currentTimeMillis()
): PokemonEntity {
    return PokemonEntity(
        id = id,
        name = name,
        height = height,
        weight = weight,
        spriteUrl = sprites.frontDefault,
        artworkUrl = sprites.other?.officialArtwork?.frontDefault,
        description = description?.replace("\n", " ")?.replace("\u000c", " ")?.trim(),
        genus = genus,
        isFavorite = isFavorite,
        lastUpdated = lastUpdated
    )
}

fun PokemonDetailResponse.toTypeEntities(): List<PokemonTypeEntity> {
    return types.map { entry ->
        PokemonTypeEntity(
            pokemonId = id,
            typeName = entry.type.name,
            slot = entry.slot
        )
    }
}

fun PokemonDetailResponse.toAbilityEntities(): List<AbilityEntity> {
    return abilities.map { entry ->
        AbilityEntity(
            pokemonId = id,
            abilityName = entry.ability.name,
            isHidden = entry.isHidden
        )
    }
}

fun PokemonDetailResponse.toBaseStatsEntity(): BaseStatsEntity {
    val statsMap = stats.associate { it.stat.name to it.baseStat }
    return BaseStatsEntity(
        pokemonId = id,
        hp = statsMap["hp"] ?: 0,
        attack = statsMap["attack"] ?: 0,
        defense = statsMap["defense"] ?: 0,
        specialAttack = statsMap["special-attack"] ?: 0,
        specialDefense = statsMap["special-defense"] ?: 0,
        speed = statsMap["speed"] ?: 0
    )
}
