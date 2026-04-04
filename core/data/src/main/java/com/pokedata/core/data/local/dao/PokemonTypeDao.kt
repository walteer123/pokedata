package com.pokedata.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokedata.core.data.local.entity.PokemonTypeEntity

@Dao
interface PokemonTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: PokemonTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<PokemonTypeEntity>)

    @Query("DELETE FROM pokemon_types WHERE pokemon_id = :pokemonId")
    suspend fun deleteByPokemonId(pokemonId: Int)

    @Query("SELECT * FROM pokemon_types WHERE pokemon_id = :pokemonId ORDER BY slot ASC")
    suspend fun getByPokemonId(pokemonId: Int): List<PokemonTypeEntity>
}
