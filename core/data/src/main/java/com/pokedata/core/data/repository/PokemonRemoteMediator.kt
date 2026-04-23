package com.pokedata.core.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.dao.RemoteKeyDao
import com.pokedata.core.data.local.entity.PokemonWithTypes
import com.pokedata.core.data.local.entity.RemoteKey
import com.pokedata.core.data.local.PokemonDatabase
import com.pokedata.core.data.remote.PokemonApi
import com.pokedata.core.data.mapper.toPokemonEntity
import com.pokedata.core.data.mapper.toTypeEntities
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokemonApi,
    private val database: PokemonDatabase,
    private val pokemonDao: PokemonDao,
    private val pokemonTypeDao: PokemonTypeDao,
    private val remoteKeyDao: RemoteKeyDao
) : RemoteMediator<Int, PokemonWithTypes>() {

    private var savedFavoriteIds: Set<Int> = emptySet()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonWithTypes>
    ): MediatorResult {
        val pageSize = state.config.pageSize
        Log.d(TAG, "load() called: loadType=$loadType, pageSize=$pageSize, anchorPosition=${state.anchorPosition}")

        val offset = when (loadType) {
            LoadType.REFRESH -> {
                Log.d(TAG, "REFRESH: starting from offset 0, preserving existing data")
                savedFavoriteIds = pokemonDao.getFavoriteIds().toSet()
                0
            }
            LoadType.PREPEND -> {
                Log.d(TAG, "PREPEND: endOfPaginationReached=true")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = remoteKeyDao.getRemoteKey(KEY)
                if (remoteKey == null) {
                    val count = pokemonDao.getPokemonCount()
                    Log.d(TAG, "APPEND: no remoteKey, db count=$count")
                    if (count == 0) 0 else count
                } else {
                    Log.d(TAG, "APPEND: remoteKey.nextOffset=${remoteKey.nextOffset}")
                    remoteKey.nextOffset
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
        }

        return try {
            Log.d(TAG, "API call: limit=$pageSize, offset=$offset")
            val response = api.getPokemonList(limit = pageSize, offset = offset)

            val endOfPaginationReached = response.next == null || response.results.isEmpty()
            Log.d(TAG, "API response: count=${response.count}, results=${response.results.size}, next=${response.next != null}, endOfPaginationReached=$endOfPaginationReached")

            val entities = response.results.map { summary ->
                val id = extractIdFromUrl(summary.url)
                summary.toPokemonEntity(
                    id = id,
                    spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
                )
            }

            val typeDataByPokemonId = coroutineScope {
                response.results.map { summary ->
                    async {
                        val id = extractIdFromUrl(summary.url)
                        try {
                            val detail = api.getPokemonDetail(id)
                            id to detail.toTypeEntities()
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to fetch detail for pokemon $id: ${e.message}")
                            id to emptyList()
                        }
                    }
                }.associate { it.await() }
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "REFRESH: preserving existing data, saved ${savedFavoriteIds.size} favorite IDs")
                }

                if (entities.isNotEmpty()) {
                    pokemonDao.insertPokemonListIgnoreConflict(entities)

                    for ((_, typeEntities) in typeDataByPokemonId) {
                        if (typeEntities.isNotEmpty()) {
                            pokemonTypeDao.insertAll(typeEntities)
                        }
                    }

                    Log.d(TAG, "Inserted ${entities.size} entities with type data")
                }

                for (id in savedFavoriteIds) {
                    pokemonDao.updateFavoriteStatus(id, true)
                }

                val newNextOffset = if (endOfPaginationReached) null else offset + pageSize
                remoteKeyDao.insertOrReplace(
                    RemoteKey(
                        label = KEY,
                        nextOffset = newNextOffset
                    )
                )
                Log.d(TAG, "Updated remoteKey: nextOffset=$newNextOffset")
            }

            Log.d(TAG, "Returning: endOfPaginationReached=$endOfPaginationReached")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Log.e(TAG, "IOException: ${e.message}", e)
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HttpException: ${e.message}", e)
            MediatorResult.Error(e)
        }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
    }

    companion object {
        private const val TAG = "PokemonRemoteMediator"
        const val KEY = "pokemon_list"
    }
}