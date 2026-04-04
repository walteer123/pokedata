package com.pokedata.feature.favorites.domain

import app.cash.turbine.test
import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: FakePokemonRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addPokemon(TestData.bulbasaurListItem)
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `invoke toggles favorite status from false to true`() = runTest {
        assertFalse(TestData.bulbasaurListItem.isFavorite)

        useCase(1)

        repository.getFavorites().test {
            val favorites = awaitItem()
            val bulbasaur = favorites.find { it.id == 1 }
            assertTrue(bulbasaur?.isFavorite == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke toggles favorite status from true to false`() = runTest {
        val charmander = TestData.charmanderListItem
        assertTrue(charmander.isFavorite)

        repository.addPokemon(charmander)

        useCase(4)

        repository.getFavorites().test {
            val favorites = awaitItem()
            val charmanderResult = favorites.find { it.id == 4 }
            assertTrue(charmanderResult == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke does nothing for non-existent Pokemon`() = runTest {
        useCase(999)
    }
}
