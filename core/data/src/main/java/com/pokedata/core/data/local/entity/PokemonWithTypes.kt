package com.pokedata.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PokemonWithTypes(
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemon_id"
    )
    val types: List<PokemonTypeEntity>
)
