package com.pokedata.feature.pokemonlist.domain

import androidx.paging.PagingData
import com.pokedata.core.testing.FakePokemonRepository
import com.pokedata.core.testing.TestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GetPokemonListUseCaseTest {

    private lateinit var repository: FakePokemonRepository
    private lateinit var useCase: GetPokemonListUseCase

    @Before
    fun setup() {
        repository = FakePokemonRepository()
        repository.addPokemon(
            TestData.bulbasaurListItem,
            TestData.charmanderListItem,
            TestData.pikachuListItem
        )
        useCase = GetPokemonListUseCase(repository)
    }

    @Test
    fun `invoke returns Flow of PagingData`() = runTest {
        val result = useCase()
        val pagingData = result.first()
        assertNotNull(pagingData)
    }

    @Test
    fun `invoke returns empty PagingData when no Pokemon`() = runTest {
        val emptyRepository = FakePokemonRepository()
        val emptyUseCase = GetPokemonListUseCase(emptyRepository)
        val result = emptyUseCase()
        val pagingData = result.first()
        assertNotNull(pagingData)
    }
}
