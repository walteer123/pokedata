package com.pokedata.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pokedata.core.data.local.dao.AbilityDao
import com.pokedata.core.data.local.dao.BaseStatsDao
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.entity.AbilityEntity
import com.pokedata.core.data.local.entity.BaseStatsEntity
import com.pokedata.core.data.local.entity.PokemonEntity
import com.pokedata.core.data.local.entity.PokemonTypeEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonTypeEntity::class,
        AbilityEntity::class,
        BaseStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): PokemonTypeDao
    abstract fun abilityDao(): AbilityDao
    abstract fun baseStatsDao(): BaseStatsDao

    companion object {
        private const val DATABASE_NAME = "pokemon_database"

        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun getInstance(context: Context): PokemonDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
