package com.pokedata.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pokemon_types",
    primaryKeys = ["pokemon_id", "slot"],
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
data class PokemonTypeEntity(
    @ColumnInfo(name = "pokemon_id") val pokemonId: Int,
    @ColumnInfo(name = "type_name") val typeName: String,
    @ColumnInfo(name = "slot") val slot: Int
)
