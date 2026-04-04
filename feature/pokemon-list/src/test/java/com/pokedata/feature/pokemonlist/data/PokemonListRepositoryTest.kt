package com.pokedata.feature.pokemonlist.data

import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokemonListRepositoryTest {

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
    fun `getPokemonList returns non-null Flow`() {
        val result = repository.getPokemonList()
        assertTrue(result != null)
    }

    @Test
    fun `fetchInitialPokemonList does not throw`() = runTest {
        repository.fetchInitialPokemonList()
    }

    @Test
    fun `refreshPokemonList does not throw`() = runTest {
        repository.refreshPokemonList()
    }

    @Test
    fun `toggleFavorite toggles favorite status`() = runTest {
        repository.toggleFavorite(1)
        val pokemon = repository.searchPokemon("bulbasaur")
        assertTrue(pokemon[0].isFavorite)
    }

    @Test
    fun `searchPokemon returns matching results`() = runTest {
        val results = repository.searchPokemon("bulba")

        assertEquals(1, results.size)
        assertEquals("bulbasaur", results[0].name)
    }

    @Test
    fun `searchPokemon by id returns matching result`() = runTest {
        val results = repository.searchPokemon("4")

        assertEquals(1, results.size)
        assertEquals("charmander", results[0].name)
    }

    @Test
    fun `getFavorites returns only favorited Pokemon`() = runTest {
        val favorites = repository.getFavorites()
        var result: List<com.pokedata.core.data.model.PokemonListItem>? = null
        favorites.collect {
            result = it
        }
        assertEquals(1, result?.size)
        assertEquals("charmander", result?.get(0)?.name)
    }

    @Test
    fun `searchPokemon returns empty list for non-existent query`() = runTest {
        val results = repository.searchPokemon("nonexistent")

        assertTrue(results.isEmpty())
    }
}
