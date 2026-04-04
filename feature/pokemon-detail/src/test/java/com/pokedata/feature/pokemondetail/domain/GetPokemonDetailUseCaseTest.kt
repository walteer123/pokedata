package com.pokedata.feature.pokemondetail.domain

import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GetPokemonDetailUseCaseTest {

    private lateinit var repository: FakePokemonRepository
    private lateinit var useCase: GetPokemonDetailUseCase

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addDetail(TestData.bulbasaurDetail)
        useCase = GetPokemonDetailUseCase(repository)
    }

    @Test
    fun `invoke returns PokemonDetail for valid id`() = runTest {
        val result = useCase(1)

        assertNotNull(result)
        assertEquals("bulbasaur", result.name)
        assertEquals(1, result.id)
        assertEquals(2, result.types.size)
        assertEquals(2, result.abilities.size)
        assertEquals(6, result.stats.size)
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke throws for invalid id`() = runTest {
        useCase(999)
    }
}
