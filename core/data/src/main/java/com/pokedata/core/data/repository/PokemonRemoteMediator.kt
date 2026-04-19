package com.pokedata.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.RemoteKeyDao
import com.pokedata.core.data.local.entity.PokemonWithTypes
import com.pokedata.core.data.local.entity.RemoteKey
import com.pokedata.core.data.local.PokemonDatabase
import com.pokedata.core.data.remote.PokemonApi
import com.pokedata.core.data.mapper.toPokemonEntity
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokemonApi,
    private val database: PokemonDatabase,
    private val pokemonDao: PokemonDao,
    private val remoteKeyDao: RemoteKeyDao
) : RemoteMediator<Int, PokemonWithTypes>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonWithTypes>
    ): MediatorResult {
        val pageSize = state.config.pageSize
        val offset = when (loadType) {
            LoadType.REFRESH -> {
                // Sempre buscar da API no REFRESH (pull-to-refresh ou primeira carga)
                // O offset é sempre 0 para refresh
                0
            }
            LoadType.PREPEND -> {
                // Não carregamos dados antes do início
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = remoteKeyDao.getRemoteKey(KEY)
                // Se não houver remoteKey, assumimos que é a primeira carga e começamos do offset 0
                // Isso pode acontecer se o APPEND for chamado antes do REFRESH completar
                if (remoteKey == null) {
                    // Verificar se já temos dados no banco
                    val count = pokemonDao.getPokemonCount()
                    if (count == 0) {
                        // Se não há dados, começamos do início
                        0
                    } else {
                        // Se há dados mas não há remoteKey, calculamos o próximo offset baseado na contagem
                        count
                    }
                } else {
                    remoteKey.nextOffset 
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
        }

        return try {
            // Buscar dados da API
            val response = api.getPokemonList(limit = pageSize, offset = offset)
            
            // Verificar se chegamos ao fim da paginação
            // A API retorna 'next' como null quando não há mais páginas
            val endOfPaginationReached = response.next == null || response.results.isEmpty()
            
            // Converter resposta para entidades
            val entities = response.results.map { summary ->
                val id = extractIdFromUrl(summary.url)
                summary.toPokemonEntity(
                    id = id,
                    spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
                )
            }

            // Salvar no banco em uma transação
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Limpar remote keys mas manter os dados existentes
                    // para preservar favoritos e evitar flash de conteúdo vazio
                    remoteKeyDao.clearRemoteKeys()
                }
                
                // Usar IGNORE para preservar dados existentes (incluindo favoritos)
                // em vez de REPLACE que sobrescreveria isFavorite = false
                if (entities.isNotEmpty()) {
                    pokemonDao.insertPokemonListIgnoreConflict(entities)
                }
                
                // Sempre atualizar o remoteKey, mesmo se não houver resultados
                // para garantir que o APPEND funcione corretamente
                val newNextOffset = if (endOfPaginationReached) null else offset + pageSize
                remoteKeyDao.insertOrReplace(
                    RemoteKey(
                        label = KEY,
                        nextOffset = newNextOffset
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: CancellationException) {
            // Propagar CancellationException para que coroutines possam ser canceladas corretamente
            throw e
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
    }

    companion object {
        const val KEY = "pokemon_list"
    }
}
