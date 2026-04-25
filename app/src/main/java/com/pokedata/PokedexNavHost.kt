package com.pokedata

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.pokedata.core.designsystem.components.AdaptiveNavSuiteScaffold
import com.pokedata.core.navigation.AppNavHost
import com.pokedata.core.navigation.Route
import com.pokedata.core.ui.rememberWindowWidthSizeClass
import com.pokedata.feature.favorites.presentation.FavoritesScreen
import com.pokedata.feature.pokemondetail.presentation.PokemonDetailScreen
import com.pokedata.feature.pokemonlist.presentation.PokemonListScreen
import com.pokedata.feature.search.presentation.SearchScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexNavHost(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val currentRoute: Route =
        when {
            currentDestination?.startsWith(
                "com.pokedata.core.navigation.Route.PokemonList"
            ) == true -> Route.PokemonList
            currentDestination?.startsWith(
                "com.pokedata.core.navigation.Route.Search"
            ) == true -> Route.Search
            currentDestination?.startsWith(
                "com.pokedata.core.navigation.Route.Favorites"
            ) == true -> Route.Favorites
            else -> Route.PokemonList
        }

    val windowWidthSizeClass = rememberWindowWidthSizeClass()

    AdaptiveNavSuiteScaffold(
        currentRouteClass = currentRoute::class,
        onNavigate = { navItem ->
            navController.navigate(navItem.route) {
                popUpTo<Route.PokemonList> { inclusive = false }
                launchSingleTop = true
            }
        }
    ) {
        SharedTransitionLayout {
            AppNavHost(
                navController = navController,
                startDestination = Route.PokemonList
            ) {
                composable<Route.PokemonList> {
                    val isExpanded = windowWidthSizeClass == WindowWidthSizeClass.Expanded
                    var selectedPokemonId by rememberSaveable { mutableStateOf<Int?>(null) }
                    var selectedSpriteUrl by rememberSaveable { mutableStateOf<String?>(null) }
                    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }

                    if (isExpanded) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                PokemonListScreen(
                                    onPokemonClick = { pokemonId, spriteUrl, name ->
                                        selectedPokemonId = pokemonId
                                        selectedSpriteUrl = spriteUrl
                                        selectedName = name
                                    },
                                    onSearchClick = {
                                        navController.navigate(Route.Search) {
                                            popUpTo<Route.PokemonList> { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    onFavoritesClick = {
                                        navController.navigate(Route.Favorites) {
                                            popUpTo<Route.PokemonList> { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@composable,
                                    windowWidthSizeClass = windowWidthSizeClass
                                )
                            }
                            HorizontalDivider(modifier = Modifier.width(1.dp))
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                if (selectedPokemonId != null) {
                                    PokemonDetailScreen(
                                        pokemonId = selectedPokemonId!!,
                                        spriteUrl = selectedSpriteUrl ?: "",
                                        pokemonName = selectedName ?: "",
                                        onBackClick = { selectedPokemonId = null },
                                        onFavoriteToggle = {},
                                        windowWidthSizeClass = windowWidthSizeClass,
                                        isInPane = true
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        androidx.compose.material3.Text(
                                            text = "Select a Pokemon",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            PokemonListScreen(
                                onPokemonClick = { pokemonId, spriteUrl, name ->
                                    navController.navigate(Route.PokemonDetail(pokemonId, spriteUrl, name))
                                },
                                onSearchClick = {
                                    navController.navigate(Route.Search) {
                                        popUpTo<Route.PokemonList> { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onFavoritesClick = {
                                    navController.navigate(Route.Favorites) {
                                        popUpTo<Route.PokemonList> { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                windowWidthSizeClass = windowWidthSizeClass
                            )
                        }
                    }
                }

                composable<Route.PokemonDetail>(
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(300)) },
                    popEnterTransition = { fadeIn(tween(300)) },
                    popExitTransition = { fadeOut(tween(300)) }
                ) { backStackEntry ->
                    val detail = backStackEntry.toRoute<Route.PokemonDetail>()
                    PokemonDetailScreen(
                        pokemonId = detail.pokemonId,
                        spriteUrl = detail.spriteUrl,
                        pokemonName = detail.name,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onFavoriteToggle = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        windowWidthSizeClass = windowWidthSizeClass
                    )
                }

                composable<Route.Search>(
                    enterTransition = { slideInHorizontally(tween(300)) + fadeIn(tween(300)) },
                    exitTransition = { slideOutHorizontally(tween(300)) + fadeOut(tween(300)) },
                    popEnterTransition = { slideInHorizontally(tween(300)) + fadeIn(tween(300)) },
                    popExitTransition = { slideOutHorizontally(tween(300)) + fadeOut(tween(300)) }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SearchScreen(
                                onPokemonClick = { pokemonId, spriteUrl, name ->
                                    navController.navigate(Route.PokemonDetail(pokemonId, spriteUrl, name))
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                windowWidthSizeClass = windowWidthSizeClass
                            )
                        }
                    }
                }

                composable<Route.Favorites> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            FavoritesScreen(
                                onPokemonClick = { pokemonId, spriteUrl, name ->
                                    navController.navigate(Route.PokemonDetail(pokemonId, spriteUrl, name))
                                },
                                onBackClick = {},
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                windowWidthSizeClass = windowWidthSizeClass
                            )
                        }
                    }
                }
            }
        }
    }
}
