package com.pokedata.core.data.mapper

import com.pokedata.core.data.local.entity.AbilityEntity
import com.pokedata.core.data.local.entity.BaseStatsEntity
import com.pokedata.core.data.local.entity.PokemonEntity
import com.pokedata.core.data.local.entity.PokemonTypeEntity
import com.pokedata.core.data.model.PokemonAbility
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.model.PokemonType

fun PokemonEntity.toPokemonListItem(): PokemonListItem {
    return PokemonListItem(
        id = id,
        name = name,
        spriteUrl = spriteUrl,
        isFavorite = isFavorite
    )
}

fun PokemonEntity.toPokemonDetail(
    types: List<PokemonTypeEntity>,
    abilities: List<AbilityEntity>,
    stats: BaseStatsEntity?
): PokemonDetail {
    return PokemonDetail(
        id = id,
        name = name,
        height = height,
        weight = weight,
        spriteUrl = spriteUrl,
        artworkUrl = artworkUrl,
        description = description,
        genus = genus,
        types = types.sortedBy { it.slot }.map { it.toDomain() },
        abilities = abilities.map { it.toDomain() },
        stats = stats?.toMap() ?: emptyMap(),
        isFavorite = isFavorite
    )
}

fun PokemonTypeEntity.toDomain(): PokemonType {
    return PokemonType(
        name = typeName,
        slot = slot
    )
}

fun AbilityEntity.toDomain(): PokemonAbility {
    return PokemonAbility(
        name = abilityName,
        isHidden = isHidden
    )
}

fun BaseStatsEntity.toMap(): Map<String, Int> {
    return mapOf(
        "hp" to hp,
        "attack" to attack,
        "defense" to defense,
        "special-attack" to specialAttack,
        "special-defense" to specialDefense,
        "speed" to speed
    )
}
