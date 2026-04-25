package com.pokedata.feature.favorites.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokedata.core.designsystem.components.EmptyState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.PokemonCard
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onPokemonClick: (Int, String, String) -> Unit,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: FavoritesViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.favorites.isEmpty() -> EmptyState(
                    title = "No favorites yet",
                    subtitle = "Tap the heart icon on a Pokemon to add it"
                )
                else -> {
                    val useGrid = windowWidthSizeClass == WindowWidthSizeClass.Medium ||
                            windowWidthSizeClass == WindowWidthSizeClass.Expanded
                    if (useGrid) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(160.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = uiState.favorites.size,
                                key = { index -> uiState.favorites[index].id }
                            ) { index ->
                                val pokemon = uiState.favorites[index]
                                val isRemoving = uiState.removingIds.contains(pokemon.id)

                                AnimatedVisibility(
                                    visible = !isRemoving,
                                    enter = fadeIn(animationSpec = tween(300)) +
                                            expandVertically(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(300)) +
                                            shrinkVertically(animationSpec = tween(300))
                                ) {
                                    with(sharedTransitionScope) {
                                        PokemonCard(
                                            id = pokemon.id,
                                            name = pokemon.name,
                                            number = pokemon.id,
                                            spriteUrl = pokemon.spriteUrl,
                                            isFavorite = true,
                                            onClick = { onPokemonClick(pokemon.id, pokemon.spriteUrl ?: "", pokemon.name) },
                                            onFavoriteToggle = { id -> viewModel.removeFavorite(id) },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            imageModifier = Modifier.sharedBounds(
                                                rememberSharedContentState(key = "pokemon-image-${pokemon.id}"),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(state = listState) {
                            items(
                                count = uiState.favorites.size,
                                key = { index -> uiState.favorites[index].id }
                            ) { index ->
                                val pokemon = uiState.favorites[index]
                                val isRemoving = uiState.removingIds.contains(pokemon.id)

                                AnimatedVisibility(
                                    visible = !isRemoving,
                                    enter = fadeIn(animationSpec = tween(300)) +
                                            expandVertically(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(300)) +
                                            shrinkVertically(animationSpec = tween(300))
                                ) {
                                    with(sharedTransitionScope) {
                                        PokemonCard(
                                            id = pokemon.id,
                                            name = pokemon.name,
                                            number = pokemon.id,
                                            spriteUrl = pokemon.spriteUrl,
                                            isFavorite = true,
                                            onClick = { onPokemonClick(pokemon.id, pokemon.spriteUrl ?: "", pokemon.name) },
                                            onFavoriteToggle = { id -> viewModel.removeFavorite(id) },
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                            imageModifier = Modifier.sharedBounds(
                                                rememberSharedContentState(key = "pokemon-image-${pokemon.id}"),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
