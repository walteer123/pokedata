package com.pokedata.feature.favorites.domain

import app.cash.turbine.test
import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFavoritesUseCaseTest {

    private lateinit var repository: FakePokemonRepository
    private lateinit var useCase: GetFavoritesUseCase

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addPokemon(
            TestData.bulbasaurListItem,
            TestData.charmanderListItem,
            TestData.pikachuListItem
        )
        useCase = GetFavoritesUseCase(repository)
    }

    @Test
    fun `invoke returns only favorited Pokemon`() = runTest {
        useCase().test {
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            assertEquals("charmander", favorites[0].name)
            assertTrue(favorites.all { it.isFavorite })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns empty list when no favorites`() = runTest {
        val emptyRepo = FakePokemonRepository()
        val emptyUseCase = GetFavoritesUseCase(emptyRepo)

        emptyUseCase().test {
            val favorites = awaitItem()
            assertTrue(favorites.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
