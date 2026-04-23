package com.pokedata.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pokedata.core.data.local.PokemonDatabase
import com.pokedata.core.data.local.dao.PokemonDao
import com.pokedata.core.data.local.dao.PokemonTypeDao
import com.pokedata.core.data.local.dao.RemoteKeyDao
import com.pokedata.core.data.local.entity.RemoteKey
import com.pokedata.core.data.remote.PokemonApi
import com.pokedata.core.data.remote.dto.NamedApiResource
import com.pokedata.core.data.remote.dto.PokemonDetailResponse
import com.pokedata.core.data.remote.dto.PokemonListResponse
import com.pokedata.core.data.remote.dto.PokemonSprites
import com.pokedata.core.data.remote.dto.PokemonSummary
import com.pokedata.core.data.remote.dto.PokemonTypeEntry
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediatorTest {

    private lateinit var api: PokemonApi
    private lateinit var database: PokemonDatabase
    private lateinit var pokemonDao: PokemonDao
    private lateinit var pokemonTypeDao: PokemonTypeDao
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var mediator: PokemonRemoteMediator

    @Before
    fun setup() {
        api = mockk()
        database = mockk()
        pokemonDao = mockk()
        pokemonTypeDao = mockk()
        remoteKeyDao = mockk()

        mediator = PokemonRemoteMediator(
            api = api,
            database = database,
            pokemonDao = pokemonDao,
            pokemonTypeDao = pokemonTypeDao,
            remoteKeyDao = remoteKeyDao
        )

        // Mock Room's withTransaction extension to execute lambda immediately
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction(any<suspend () -> Unit>()) } coAnswers {
            secondArg<suspend () -> Unit>().invoke()
        }

        // Mock Android Log to avoid RuntimeException in unit tests
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `refresh does not clear existing data`() = runTest {
        // Given
        val response = PokemonListResponse(
            count = 2,
            next = null,
            previous = null,
            results = listOf(
                PokemonSummary(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
            )
        )
        val detailResponse = PokemonDetailResponse(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = PokemonSprites(frontDefault = null, other = null),
            stats = emptyList(),
            types = listOf(PokemonTypeEntry(slot = 1, type = NamedApiResource("grass", ""))),
            abilities = emptyList(),
            species = NamedApiResource("bulbasaur", "")
        )

        coEvery { api.getPokemonList(limit = 20, offset = 0) } returns response
        coEvery { api.getPokemonDetail(1) } returns detailResponse
        coEvery { pokemonDao.getFavoriteIds() } returns emptyList()
        coEvery { pokemonDao.insertPokemonListIgnoreConflict(any()) } just Runs
        coEvery { pokemonTypeDao.insertAll(any()) } just Runs
        coEvery { remoteKeyDao.insertOrReplace(any()) } just Runs

        val pagingState = PagingState<Int, com.pokedata.core.data.local.entity.PokemonWithTypes>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        // endOfPaginationReached is true because response.next is null in this test
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        // Verify clearAll was NOT called
        coVerify(exactly = 0) { pokemonDao.clearAll() }
        coVerify(exactly = 0) { remoteKeyDao.clearRemoteKeys() }

        // Verify insert was called
        coVerify(exactly = 1) { pokemonDao.insertPokemonListIgnoreConflict(any()) }
    }

    @Test
    fun `refresh preserves existing favorites`() = runTest {
        // Given
        val response = PokemonListResponse(
            count = 2,
            next = null,
            previous = null,
            results = listOf(
                PokemonSummary(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
            )
        )
        val detailResponse = PokemonDetailResponse(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = PokemonSprites(frontDefault = null, other = null),
            stats = emptyList(),
            types = listOf(PokemonTypeEntry(slot = 1, type = NamedApiResource("grass", ""))),
            abilities = emptyList(),
            species = NamedApiResource("bulbasaur", "")
        )

        coEvery { api.getPokemonList(limit = 20, offset = 0) } returns response
        coEvery { api.getPokemonDetail(1) } returns detailResponse
        coEvery { pokemonDao.getFavoriteIds() } returns listOf(1)
        coEvery { pokemonDao.insertPokemonListIgnoreConflict(any()) } just Runs
        coEvery { pokemonDao.updateFavoriteStatus(any(), any()) } just Runs
        coEvery { pokemonTypeDao.insertAll(any()) } just Runs
        coEvery { remoteKeyDao.insertOrReplace(any()) } just Runs

        val pagingState = PagingState<Int, com.pokedata.core.data.local.entity.PokemonWithTypes>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)

        // Verify favorite was restored
        coVerify(exactly = 1) { pokemonDao.updateFavoriteStatus(1, true) }
    }

    @Test
    fun `append loads next page when remote key exists`() = runTest {
        // Given
        val response = PokemonListResponse(
            count = 40,
            next = "https://pokeapi.co/api/v2/pokemon?offset=40&limit=20",
            previous = null,
            results = listOf(
                PokemonSummary(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
            )
        )
        val detailResponse = PokemonDetailResponse(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = PokemonSprites(frontDefault = null, other = null),
            stats = emptyList(),
            types = listOf(PokemonTypeEntry(slot = 1, type = NamedApiResource("grass", ""))),
            abilities = emptyList(),
            species = NamedApiResource("bulbasaur", "")
        )

        coEvery { api.getPokemonList(limit = 20, offset = 20) } returns response
        coEvery { api.getPokemonDetail(1) } returns detailResponse
        coEvery { remoteKeyDao.getRemoteKey(PokemonRemoteMediator.KEY) } returns RemoteKey(
            label = PokemonRemoteMediator.KEY,
            nextOffset = 20
        )
        coEvery { pokemonDao.getFavoriteIds() } returns emptyList()
        coEvery { pokemonDao.insertPokemonListIgnoreConflict(any()) } just Runs
        coEvery { pokemonTypeDao.insertAll(any()) } just Runs
        coEvery { remoteKeyDao.insertOrReplace(any()) } just Runs

        val pagingState = PagingState<Int, com.pokedata.core.data.local.entity.PokemonWithTypes>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.APPEND, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `append reaches end when remote key nextOffset is null`() = runTest {
        // Given
        coEvery { remoteKeyDao.getRemoteKey(PokemonRemoteMediator.KEY) } returns RemoteKey(
            label = PokemonRemoteMediator.KEY,
            nextOffset = null
        )

        val pagingState = PagingState<Int, com.pokedata.core.data.local.entity.PokemonWithTypes>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.APPEND, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `prepend returns endOfPaginationReached immediately`() = runTest {
        val pagingState = PagingState<Int, com.pokedata.core.data.local.entity.PokemonWithTypes>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.PREPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}
