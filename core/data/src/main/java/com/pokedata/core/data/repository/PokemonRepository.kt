package com.pokedata.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.pokedata.core.data.local.PokemonDatabase
import com.pokedata.core.data.local.dao.AbilityDao
import com.pokedata.core.data.local.dao.BaseStatsDao
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.dao.RemoteKeyDao
import com.pokedata.core.data.local.entity.PokemonEntity
import androidx.room.withTransaction
import com.pokedata.core.data.mapper.toAbilityEntities
import com.pokedata.core.data.mapper.toBaseStatsEntity
import com.pokedata.core.data.mapper.toPokemonDetail
import com.pokedata.core.data.mapper.toPokemonEntity
import com.pokedata.core.data.mapper.toTypeEntities
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonAbility
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.model.PokemonType
import com.pokedata.core.data.remote.PokemonApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class PokemonRepository(
    private val api: PokemonApi,
    private val database: PokemonDatabase,
    private val pokemonDao: PokemonDao,
    private val pokemonTypeDao: PokemonTypeDao,
    private val abilityDao: AbilityDao,
    private val baseStatsDao: BaseStatsDao,
    private val remoteKeyDao: RemoteKeyDao
) : PokemonRepositoryInterface {

    override fun getPokemonList(typeFilter: String?): Flow<PagingData<PokemonListItem>> {
        val remoteMediator = PokemonRemoteMediator(
            api = api,
            database = database,
            pokemonDao = pokemonDao,
            pokemonTypeDao = pokemonTypeDao,
            remoteKeyDao = remoteKeyDao
        )
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PAGE_SIZE
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                if (typeFilter != null) {
                    pokemonDao.getPokemonWithTypesByTypePagingSource(typeFilter)
                } else {
                    pokemonDao.getPokemonWithTypesPagingSource()
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { pokemonWithTypes ->
                PokemonListItem(
                    id = pokemonWithTypes.pokemon.id,
                    name = pokemonWithTypes.pokemon.name,
                    spriteUrl = pokemonWithTypes.pokemon.spriteUrl,
                    isFavorite = pokemonWithTypes.pokemon.isFavorite,
                    types = pokemonWithTypes.types.map { it.typeName }.sorted()
                )
            }
        }
    }

    override suspend fun fetchInitialPokemonList() {
        // Com RemoteMediator, o carregamento inicial é automático
        // Este método é mantido para compatibilidade
    }

    override suspend fun refreshPokemonList() {
        // Com RemoteMediator, o refresh é automático via Paging library
        // Este método é mantido para compatibilidade
    }

    override suspend fun fetchAndCachePokemonList(offset: Int, limit: Int) {
        // Com RemoteMediator, o carregamento paginado é automático
        // Este método é mantido para compatibilidade
    }

    override suspend fun getPokemonDetail(id: Int): PokemonDetail = withContext(Dispatchers.IO) {
        val cached = pokemonDao.getPokemonById(id)
        val needsRefresh = cached == null || isStale(cached.lastUpdated) || cached.height == 0 || cached.weight == 0

        if (needsRefresh) {
            fetchAndCachePokemonDetail(id, cached?.isFavorite ?: false)
        }

        loadPokemonDetailFromDb(id)
    }

    private suspend fun fetchAndCachePokemonDetail(id: Int, currentFavorite: Boolean) {
        val (detail, species) = coroutineScope {
            val detailDeferred = async { api.getPokemonDetail(id) }
            val speciesDeferred = async { api.getPokemonSpecies(id) }
            detailDeferred.await() to speciesDeferred.await()
        }

        val englishFlavorText = species.flavorTextEntries
            .firstOrNull { it.language.name == "en" }
            ?.flavorText
            ?.replace("\n", " ")
            ?.replace("\u000c", " ")
            ?.trim()

        val englishGenus = species.genera
            .firstOrNull { it.language.name == "en" }
            ?.genus

        val entity = detail.toPokemonEntity(
            description = englishFlavorText,
            genus = englishGenus,
            isFavorite = currentFavorite
        )

        database.withTransaction {
            pokemonDao.insertPokemon(entity)
            pokemonTypeDao.deleteByPokemonId(id)
            pokemonTypeDao.insertAll(detail.toTypeEntities())
            abilityDao.deleteByPokemonId(id)
            abilityDao.insertAll(detail.toAbilityEntities())
            baseStatsDao.deleteByPokemonId(id)
            baseStatsDao.insert(detail.toBaseStatsEntity())
        }
    }

    private suspend fun loadPokemonDetailFromDb(id: Int): PokemonDetail {
        val entity = pokemonDao.getPokemonById(id)
            ?: throw IllegalStateException("Pokemon $id not found in database")

        val types = pokemonTypeDao.getByPokemonId(id)
        val abilities = abilityDao.getByPokemonId(id)
        val baseStats = baseStatsDao.getByPokemonId(id)

        return entity.toPokemonDetail(types = types, abilities = abilities, stats = baseStats)
    }

    override suspend fun toggleFavorite(id: Int) = withContext(Dispatchers.IO) {
        val entity = pokemonDao.getPokemonById(id) ?: return@withContext
        pokemonDao.updateFavoriteStatus(id, !entity.isFavorite)
    }

    override suspend fun searchPokemon(query: String): List<PokemonListItem> = withContext(Dispatchers.IO) {
        val queryInt = query.toIntOrNull()
        pokemonDao.searchPokemonWithTypes(query.lowercase(), queryInt).map { pokemonWithTypes ->
            PokemonListItem(
                id = pokemonWithTypes.pokemon.id,
                name = pokemonWithTypes.pokemon.name,
                spriteUrl = pokemonWithTypes.pokemon.spriteUrl,
                isFavorite = pokemonWithTypes.pokemon.isFavorite,
                types = pokemonWithTypes.types.map { it.typeName }.sorted()
            )
        }
    }

    override fun getFavorites(): Flow<List<PokemonListItem>> {
        return pokemonDao.getFavoritesWithTypesFlow().map { list ->
            list.map { pokemonWithTypes ->
                PokemonListItem(
                    id = pokemonWithTypes.pokemon.id,
                    name = pokemonWithTypes.pokemon.name,
                    spriteUrl = pokemonWithTypes.pokemon.spriteUrl,
                    isFavorite = pokemonWithTypes.pokemon.isFavorite,
                    types = pokemonWithTypes.types.map { it.typeName }.sorted()
                )
            }
        }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
    }

    private fun isStale(lastUpdated: Long): Boolean {
        val oneHour = 60 * 60 * 1000L
        return System.currentTimeMillis() - lastUpdated > oneHour
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
