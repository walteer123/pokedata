package com.pokedata.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pokedata.core.data.local.entity.PokemonEntity

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getPokemonPagingSource(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' OR id = :queryInt ORDER BY name ASC")
    suspend fun searchPokemon(query: String, queryInt: Int?): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE is_favorite = 1 ORDER BY name ASC")
    fun getFavoritesPagingSource(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE is_favorite = 1 ORDER BY name ASC")
    fun getFavorites(): List<PokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemon: List<PokemonEntity>)

    @Query("UPDATE pokemon SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM pokemon WHERE id = :id")
    suspend fun deletePokemon(id: Int)

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getPokemonCount(): Int
}
