package com.pokedata.feature.favorites.di

import com.pokedata.feature.favorites.domain.GetFavoritesUseCase
import com.pokedata.feature.favorites.domain.RemoveFavoriteUseCase
import com.pokedata.feature.favorites.domain.ToggleFavoriteUseCase
import com.pokedata.feature.favorites.presentation.FavoritesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val favoritesModule = module {
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::RemoveFavoriteUseCase)
    viewModelOf(::FavoritesViewModel)
}
