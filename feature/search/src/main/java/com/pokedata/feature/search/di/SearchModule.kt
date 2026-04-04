package com.pokedata.feature.search.di

import com.pokedata.feature.search.domain.SearchPokemonUseCase
import com.pokedata.feature.search.presentation.SearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    factoryOf(::SearchPokemonUseCase)
    viewModelOf(::SearchViewModel)
}
