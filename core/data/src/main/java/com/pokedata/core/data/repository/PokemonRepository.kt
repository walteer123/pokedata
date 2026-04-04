package com.pokedata.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.pokedata.core.data.local.dao.AbilityDao
import com.pokedata.core.data.local.dao.BaseStatsDao
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.entity.PokemonEntity
import com.pokedata.core.data.mapper.toAbilityEntities
import com.pokedata.core.data.mapper.toBaseStatsEntity
import com.pokedata.core.data.mapper.toPokemonEntity
import com.pokedata.core.data.mapper.toTypeEntities
import com.pokedata.core.data.model.PokemonDetail
import com.pokedata.core.data.model.PokemonAbility
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.data.model.PokemonType
import com.pokedata.core.data.remote.PokemonApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val api: PokemonApi,
    private val pokemonDao: PokemonDao,
    private val pokemonTypeDao: PokemonTypeDao,
    private val abilityDao: AbilityDao,
    private val baseStatsDao: BaseStatsDao
) : PokemonRepositoryInterface {

    override fun getPokemonList(): Flow<PagingData<PokemonListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pokemonDao.getPokemonPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                PokemonListItem(
                    id = entity.id,
                    name = entity.name,
                    spriteUrl = entity.spriteUrl,
                    isFavorite = entity.isFavorite
                )
            }
        }
    }

    override suspend fun fetchInitialPokemonList() = withContext(Dispatchers.IO) {
        val count = pokemonDao.getPokemonCount()
        if (count == 0) {
            fetchPage(offset = 0, limit = PAGE_SIZE * 5)
        }
    }

    override suspend fun refreshPokemonList() = withContext(Dispatchers.IO) {
        fetchPage(offset = 0, limit = PAGE_SIZE * 5)
    }

    private suspend fun fetchPage(offset: Int, limit: Int) {
        val response = api.getPokemonList(limit = limit, offset = offset)
        val entities = response.results.map { summary ->
            val id = extractIdFromUrl(summary.url)
            summary.toPokemonEntity(
                id = id,
                spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
            )
        }
        pokemonDao.insertPokemonList(entities)
    }

    override suspend fun fetchAndCachePokemonList(offset: Int, limit: Int) = withContext(Dispatchers.IO) {
        fetchPage(offset, limit)
    }

    override suspend fun getPokemonDetail(id: Int): PokemonDetail = withContext(Dispatchers.IO) {
        val cached = pokemonDao.getPokemonById(id)
        val needsRefresh = cached == null || isStale(cached.lastUpdated)

        if (needsRefresh) {
            fetchAndCachePokemonDetail(id, cached?.isFavorite ?: false)
        }

        loadPokemonDetailFromDb(id)
    }

    private suspend fun fetchAndCachePokemonDetail(id: Int, currentFavorite: Boolean) {
        val detail = api.getPokemonDetail(id)
        val species = api.getPokemonSpecies(id)

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

        pokemonDao.insertPokemon(entity)
        pokemonTypeDao.deleteByPokemonId(id)
        pokemonTypeDao.insertAll(detail.toTypeEntities())
        abilityDao.deleteByPokemonId(id)
        abilityDao.insertAll(detail.toAbilityEntities())
        baseStatsDao.deleteByPokemonId(id)
        baseStatsDao.insert(detail.toBaseStatsEntity())
    }

    private suspend fun loadPokemonDetailFromDb(id: Int): PokemonDetail {
        val entity = pokemonDao.getPokemonById(id)
            ?: throw IllegalStateException("Pokemon $id not found in database")

        val types = pokemonTypeDao.getByPokemonId(id)
            .map { PokemonType(name = it.typeName, slot = it.slot) }
            .sortedBy { it.slot }

        val abilities = abilityDao.getByPokemonId(id)
            .map { PokemonAbility(name = it.abilityName, isHidden = it.isHidden) }

        val baseStats = baseStatsDao.getByPokemonId(id)
        val stats = if (baseStats != null) {
            mapOf(
                "hp" to baseStats.hp,
                "attack" to baseStats.attack,
                "defense" to baseStats.defense,
                "special-attack" to baseStats.specialAttack,
                "special-defense" to baseStats.specialDefense,
                "speed" to baseStats.speed
            )
        } else {
            emptyMap()
        }

        return PokemonDetail(
            id = entity.id,
            name = entity.name,
            height = entity.height,
            weight = entity.weight,
            spriteUrl = entity.spriteUrl,
            artworkUrl = entity.artworkUrl,
            description = entity.description,
            genus = entity.genus,
            types = types,
            abilities = abilities,
            stats = stats,
            isFavorite = entity.isFavorite
        )
    }

    override suspend fun toggleFavorite(id: Int) = withContext(Dispatchers.IO) {
        val entity = pokemonDao.getPokemonById(id) ?: return@withContext
        pokemonDao.updateFavoriteStatus(id, !entity.isFavorite)
    }

    override suspend fun searchPokemon(query: String): List<PokemonListItem> = withContext(Dispatchers.IO) {
        val queryInt = query.toIntOrNull()
        pokemonDao.searchPokemon(query.lowercase(), queryInt).map { entity ->
            PokemonListItem(
                id = entity.id,
                name = entity.name,
                spriteUrl = entity.spriteUrl,
                isFavorite = entity.isFavorite
            )
        }
    }

    override fun getFavorites(): Flow<List<PokemonListItem>> {
        return kotlinx.coroutines.flow.flow {
            emit(
                withContext(Dispatchers.IO) {
                    pokemonDao.getFavorites().map { entity ->
                        PokemonListItem(
                            id = entity.id,
                            name = entity.name,
                            spriteUrl = entity.spriteUrl,
                            isFavorite = entity.isFavorite
                        )
                    }
                }
            )
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
