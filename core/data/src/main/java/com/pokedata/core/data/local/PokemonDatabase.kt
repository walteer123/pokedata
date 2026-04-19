package com.pokedata.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pokedata.core.data.local.dao.AbilityDao
import com.pokedata.core.data.local.dao.BaseStatsDao
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.dao.RemoteKeyDao
import com.pokedata.core.data.local.entity.AbilityEntity
import com.pokedata.core.data.local.entity.BaseStatsEntity
import com.pokedata.core.data.local.entity.PokemonEntity
import com.pokedata.core.data.local.entity.PokemonTypeEntity
import com.pokedata.core.data.local.entity.RemoteKey

@Database(
    entities = [
        PokemonEntity::class,
        PokemonTypeEntity::class,
        AbilityEntity::class,
        BaseStatsEntity::class,
        RemoteKey::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): PokemonTypeDao
    abstract fun abilityDao(): AbilityDao
    abstract fun baseStatsDao(): BaseStatsDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `remote_keys` (" +
                    "`label` TEXT NOT NULL, " +
                    "`nextOffset` INTEGER, " +
                    "PRIMARY KEY(`label`))"
                )
            }
        }
        private const val DATABASE_NAME = "pokemon_database"

        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun getInstance(context: Context): PokemonDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    DATABASE_NAME
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Durante desenvolvimento, permite recriar o banco se necessário
                .build().also { INSTANCE = it }
            }
        }
    }
}
