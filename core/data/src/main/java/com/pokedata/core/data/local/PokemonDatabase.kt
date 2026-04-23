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
    version = 3,
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Drop and recreate pokemon_types with composite primary key
                db.execSQL("DROP TABLE IF EXISTS `pokemon_types`")
                db.execSQL(
                    "CREATE TABLE `pokemon_types` (" +
                    "`pokemon_id` INTEGER NOT NULL, " +
                    "`type_name` TEXT NOT NULL, " +
                    "`slot` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`pokemon_id`, `slot`), " +
                    "FOREIGN KEY(`pokemon_id`) REFERENCES `pokemon`(`id`) ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX `index_pokemon_types_pokemon_id` ON `pokemon_types` (`pokemon_id`)")

                // Drop and recreate abilities with composite primary key
                db.execSQL("DROP TABLE IF EXISTS `abilities`")
                db.execSQL(
                    "CREATE TABLE `abilities` (" +
                    "`pokemon_id` INTEGER NOT NULL, " +
                    "`ability_name` TEXT NOT NULL, " +
                    "`is_hidden` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`pokemon_id`, `ability_name`), " +
                    "FOREIGN KEY(`pokemon_id`) REFERENCES `pokemon`(`id`) ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX `index_abilities_pokemon_id` ON `abilities` (`pokemon_id`)")
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration() // Durante desenvolvimento, permite recriar o banco se necessário
                .build().also { INSTANCE = it }
            }
        }
    }
}
