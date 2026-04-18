package com.pokedata

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.pokedata.core.designsystem.components.ModernBottomNav
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val showBottomNav =
        currentDestination?.startsWith("com.pokedata.core.navigation.Route.PokemonList") == true ||
            currentDestination?.startsWith("com.pokedata.core.navigation.Route.Search") == true ||
            currentDestination?.startsWith("com.pokedata.core.navigation.Route.Favorites") == true

    val currentRoute: Route =
        when {
            currentDestination?.startsWith("com.pokedata.core.navigation.Route.PokemonList") == true -> Route.PokemonList
            currentDestination?.startsWith("com.pokedata.core.navigation.Route.Search") == true -> Route.Search
            currentDestination?.startsWith("com.pokedata.core.navigation.Route.Favorites") == true -> Route.Favorites
            else -> Route.PokemonList
        }

    AppNavHost(
        navController = navController,
        startDestination = Route.PokemonList
    ) {
        composable<Route.PokemonList> {
            Scaffold(
                bottomBar = {
                    if (showBottomNav) {
                        ModernBottomNav(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo<Route.PokemonList> { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier.padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonListScreen(
                        onPokemonClick = { pokemonId ->
                            navController.navigate(Route.PokemonDetail(pokemonId))
                        },
                        onSearchClick = {},
                        onFavoritesClick = {}
                    )
                }
            }
        }

        composable<Route.PokemonDetail>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
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
            Scaffold(
                bottomBar = {
                    if (showBottomNav) {
                        ModernBottomNav(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo<Route.PokemonList> { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier.padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SearchScreen(
                            onPokemonClick = { pokemonId ->
                                navController.navigate(Route.PokemonDetail(pokemonId))
                            },
                            onBackClick = {}
                        )
                    }
                }
            }
        }

        composable<Route.Favorites> {
            Scaffold(
                bottomBar = {
                    if (showBottomNav) {
                        ModernBottomNav(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo<Route.PokemonList> { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier.padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FavoritesScreen(
                            onPokemonClick = { pokemonId ->
                                navController.navigate(Route.PokemonDetail(pokemonId))
                            },
                            onBackClick = {}
                        )
                    }
                }
            }
        }
    }
}
