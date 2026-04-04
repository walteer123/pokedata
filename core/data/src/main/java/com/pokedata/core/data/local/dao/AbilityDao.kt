package com.pokedata.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokedata.core.data.local.entity.AbilityEntity

@Dao
interface AbilityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ability: AbilityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(abilities: List<AbilityEntity>)

    @Query("DELETE FROM abilities WHERE pokemon_id = :pokemonId")
    suspend fun deleteByPokemonId(pokemonId: Int)

    @Query("SELECT * FROM abilities WHERE pokemon_id = :pokemonId")
    suspend fun getByPokemonId(pokemonId: Int): List<AbilityEntity>
}
