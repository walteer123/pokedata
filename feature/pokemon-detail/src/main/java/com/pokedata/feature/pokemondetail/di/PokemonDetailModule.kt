package com.pokedata.feature.pokemondetail.di

import com.pokedata.feature.pokemondetail.domain.GetPokemonDetailUseCase
import com.pokedata.feature.pokemondetail.presentation.PokemonDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val pokemonDetailModule = module {
    factoryOf(::GetPokemonDetailUseCase)
    viewModel { (pokemonId: Int) ->
        PokemonDetailViewModel(repository = get(), pokemonId = pokemonId)
    }
}
