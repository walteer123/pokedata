package com.pokedata.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "abilities",
    primaryKeys = ["pokemon_id", "ability_name"],
    foreignKeys = [
        ForeignKey(
            entity = PokemonEntity::class,
            parentColumns = ["id"],
            childColumns = ["pokemon_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pokemon_id"])]
)
data class AbilityEntity(
    @ColumnInfo(name = "pokemon_id") val pokemonId: Int,
    @ColumnInfo(name = "ability_name") val abilityName: String,
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean
)
