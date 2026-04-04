package com.pokedata

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.pokedata.core.navigation.AppNavHost
import com.pokedata.core.navigation.Route
import com.pokedata.feature.favorites.presentation.FavoritesScreen
import com.pokedata.feature.pokemondetail.presentation.PokemonDetailScreen
import com.pokedata.feature.pokemonlist.presentation.PokemonListScreen
import com.pokedata.feature.search.presentation.SearchScreen

@Composable
fun PokedexNavHost(
    navController: NavHostController
) {
    AppNavHost(
        navController = navController,
        startDestination = Route.PokemonList
    ) {
        composable<Route.PokemonList> {
            PokemonListScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(Route.PokemonDetail(pokemonId))
                },
                onSearchClick = {
                    navController.navigate(Route.Search)
                },
                onFavoritesClick = {
                    navController.navigate(Route.Favorites)
                }
            )
        }

        composable<Route.PokemonDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Route.PokemonDetail>()
            PokemonDetailScreen(
                pokemonId = detail.pokemonId,
                onBackClick = {
                    navController.popBackStack()
                },
                onFavoriteToggle = {}
            )
        }

        composable<Route.Search> {
            SearchScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(Route.PokemonDetail(pokemonId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Favorites> {
            FavoritesScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(Route.PokemonDetail(pokemonId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
