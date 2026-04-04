package com.pokedata.feature.search.presentation

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

class SearchViewModelTest {

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
    fun `initial state has empty query and results`() {
        val viewModel = SearchViewModel(repository)
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasNoResults)
    }

    @Test
    fun `onQueryChange updates query in state`() {
        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("bulba")
        val state = viewModel.uiState.value
        assertEquals("bulba", state.query)
    }

    @Test
    fun `clearSearch resets state to initial values`() {
        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("bulba")
        viewModel.clearSearch()
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.hasNoResults)
    }

    @Test
    fun `blank query resets to initial state`() {
        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("bulba")
        viewModel.onQueryChange("")
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.hasNoResults)
    }
}
