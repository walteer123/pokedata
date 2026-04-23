package com.pokedata.feature.favorites.presentation

import app.cash.turbine.test
import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import com.pokedata.core.testing.TestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repository: FakePokemonRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addPokemon(
            TestData.bulbasaurListItem,
            TestData.charmanderListItem,
            TestData.pikachuListItem
        )
    }

    @Test
    fun `initial state is loading`() {
        viewModel = FavoritesViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.favorites.isEmpty())
    }

    @Test
    fun `loadFavorites loads favorited Pokemon`() = runTest {
        viewModel = FavoritesViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.favorites.size)
        assertEquals("charmander", state.favorites[0].name)
    }

    @Test
    fun `loadFavorites shows empty list when no favorites`() = runTest {
        val emptyRepo = FakePokemonRepository()
        viewModel = FavoritesViewModel(emptyRepo)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.favorites.isEmpty())
    }

    @Test
    fun `removeFavorite toggles Pokemon favorite status in repository`() = runTest {
        viewModel = FavoritesViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.favorites.size)

        viewModel.removeFavorite(4)
        dispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        repository.getFavorites().test {
            val favorites = awaitItem()
            assertTrue(favorites.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeFavorite adds id to removingIds immediately`() = runTest {
        viewModel = FavoritesViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.removeFavorite(4)

        assertTrue(viewModel.uiState.value.removingIds.contains(4))
    }

    @Test
    fun `removeFavorite does not add duplicate removingIds`() = runTest {
        viewModel = FavoritesViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.removeFavorite(4)
        viewModel.removeFavorite(4)

        assertEquals(1, viewModel.uiState.value.removingIds.size)
    }
}
