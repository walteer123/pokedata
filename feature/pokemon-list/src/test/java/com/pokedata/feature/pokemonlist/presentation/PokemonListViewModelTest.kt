package com.pokedata.feature.pokemonlist.presentation

import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import com.pokedata.core.testing.TestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PokemonListViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repository: FakePokemonRepository

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
    fun `initial state is loading`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `loadPokemon transitions to success state`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `refresh does not throw`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.refresh()
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `toggleFavorite does not throw`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleFavorite(1)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `clearError removes error message`() {
        val viewModel = PokemonListViewModel(repository)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `pokemonList returns Flow of PagingData`() {
        val viewModel = PokemonListViewModel(repository)
        assertNotNull(viewModel.pokemonList)
    }
}
