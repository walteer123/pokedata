package com.pokedata.feature.pokemonlist.presentation

import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import com.pokedata.core.testing.TestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun `initial state has no type filter selected`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        val state = viewModel.uiState.value
        assertNull(state.selectedTypeFilter)
    }

    @Test
    fun `pokemonList returns Flow of PagingData`() {
        val viewModel = PokemonListViewModel(repository)
        assertNotNull(viewModel.pokemonList)
    }

    @Test
    fun `toggleFavorite does not throw`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleFavorite(1)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `setTypeFilter updates selected type`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        
        viewModel.setTypeFilter("fire")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("fire", viewModel.uiState.value.selectedTypeFilter)
    }

    @Test
    fun `setTypeFilter to null clears filter`() = runTest {
        val viewModel = PokemonListViewModel(repository)
        
        viewModel.setTypeFilter("fire")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("fire", viewModel.uiState.value.selectedTypeFilter)
        
        viewModel.setTypeFilter(null)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedTypeFilter)
    }
}
