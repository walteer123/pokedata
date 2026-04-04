package com.pokedata.feature.pokemondetail.presentation

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

class PokemonDetailViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repository: FakePokemonRepository
    private lateinit var viewModel: PokemonDetailViewModel

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addDetail(TestData.bulbasaurDetail)
        repository.addPokemon(TestData.bulbasaurListItem)
    }

    @Test
    fun `initial state is loading`() {
        viewModel = PokemonDetailViewModel(repository, 1)

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertNull(state.pokemon)
    }

    @Test
    fun `loadPokemonDetail transitions to success state`() = runTest {
        viewModel = PokemonDetailViewModel(repository, 1)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.pokemon)
        assertEquals("bulbasaur", state.pokemon?.name)
    }

    @Test
    fun `loadPokemonDetail transitions to error state for invalid id`() = runTest {
        viewModel = PokemonDetailViewModel(repository, 999)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.pokemon)
    }

    @Test
    fun `reload reloads Pokemon detail`() = runTest {
        viewModel = PokemonDetailViewModel(repository, 1)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val firstState = viewModel.uiState.value
        assertEquals("bulbasaur", firstState.pokemon?.name)

        viewModel.reload()
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val reloadedState = viewModel.uiState.value
        assertFalse(reloadedState.isLoading)
        assertNotNull(reloadedState.pokemon)
    }

    @Test
    fun `toggleFavorite calls repository toggle and reloads`() = runTest {
        viewModel = PokemonDetailViewModel(repository, 1)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.uiState.value
        assertFalse(initialState.pokemon?.isFavorite == true)

        viewModel.toggleFavorite()
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val toggledState = viewModel.uiState.value
        assertFalse(toggledState.isLoading)
        assertNotNull(toggledState.pokemon)
    }
}
