package com.pokedata.feature.search.domain

import app.cash.turbine.test
import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchPokemonUseCaseTest {

    private lateinit var repository: FakePokemonRepository
    private lateinit var useCase: SearchPokemonUseCase

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addPokemon(
            TestData.bulbasaurListItem,
            TestData.charmanderListItem,
            TestData.pikachuListItem
        )
        useCase = SearchPokemonUseCase(repository)
    }

    @Test
    fun `invoke returns matching Pokemon for name query`() = runTest {
        useCase("bulba").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("bulbasaur", results[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns matching Pokemon for id query`() = runTest {
        useCase("4").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("charmander", results[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns empty list for blank query`() = runTest {
        useCase("").test {
            val results = awaitItem()
            assertTrue(results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns empty list for non-existent query`() = runTest {
        useCase("nonexistent").test {
            val results = awaitItem()
            assertTrue(results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke is case insensitive`() = runTest {
        useCase("BULBASAUR").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("bulbasaur", results[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
