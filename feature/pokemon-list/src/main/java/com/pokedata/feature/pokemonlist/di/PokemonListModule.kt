package com.pokedata.feature.pokemonlist.di

import com.pokedata.feature.pokemonlist.domain.GetPokemonListUseCase
import com.pokedata.feature.pokemonlist.presentation.PokemonListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val pokemonListModule = module {
    factoryOf(::GetPokemonListUseCase)
    viewModelOf(::PokemonListViewModel)
}
