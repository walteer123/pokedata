package com.pokedata.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokedata.core.data.local.entity.BaseStatsEntity

@Dao
interface BaseStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: BaseStatsEntity)

    @Query("DELETE FROM base_stats WHERE pokemon_id = :pokemonId")
    suspend fun deleteByPokemonId(pokemonId: Int)

    @Query("SELECT * FROM base_stats WHERE pokemon_id = :pokemonId")
    suspend fun getByPokemonId(pokemonId: Int): BaseStatsEntity?
}
