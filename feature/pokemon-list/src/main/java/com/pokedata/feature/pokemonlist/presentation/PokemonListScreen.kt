package com.pokedata.feature.pokemonlist.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.designsystem.components.EmptyState
import com.pokedata.core.designsystem.components.ErrorState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.PokemonCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    onPokemonClick: (Int, String, String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PokemonListViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pokemon = viewModel.pokemonList.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Pokedex",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                ),
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && pokemon.loadState.refresh is LoadState.Loading -> {
                    LoadingIndicator()
                }
                uiState.error != null && pokemon.itemCount == 0 -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = {
                            viewModel.clearError()
                            viewModel.refresh()
                        }
                    )
                }
                else -> {
                    PokemonListContent(
                        pokemon = pokemon,
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = viewModel::refresh,
                        onPokemonClick = onPokemonClick,
                        onFavoriteToggle = viewModel::toggleFavorite,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }

            if (uiState.error != null && pokemon.itemCount > 0) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonListContent(
    pokemon: LazyPagingItems<PokemonListItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onPokemonClick: (Int, String, String) -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var isPullRefreshing by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = isRefreshing || isPullRefreshing,
        onRefresh = {
            isPullRefreshing = true
            onRefresh()
            isPullRefreshing = false
        },
        state = pullToRefreshState,
        modifier = modifier
    ) {
        if (pokemon.itemCount == 0 && pokemon.loadState.refresh is LoadState.NotLoading) {
            EmptyState(
                title = "No Pokemon found",
                subtitle = "Try refreshing the list"
            )
            return@PullToRefreshBox
        }

        LazyColumn {
            items(count = pokemon.itemCount) { index ->
                val pokemonItem = pokemon[index]
                if (pokemonItem != null) {
                    with(sharedTransitionScope) {
                        PokemonCard(
                            id = pokemonItem.id,
                            name = pokemonItem.name,
                            number = pokemonItem.id,
                            spriteUrl = pokemonItem.spriteUrl,
                            isFavorite = pokemonItem.isFavorite,
                            onClick = { onPokemonClick(pokemonItem.id, pokemonItem.spriteUrl ?: "", pokemonItem.name) },
                            onFavoriteToggle = onFavoriteToggle,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            imageModifier = Modifier.sharedBounds(
                                rememberSharedContentState(key = "pokemon-image-${pokemonItem.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        )
                    }
                }
            }

            item {
                when (pokemon.loadState.append) {
                    is LoadState.Loading -> {
                        LoadingIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is LoadState.Error -> {
                        // Silently ignore append errors, user can pull to refresh
                    }
                    else -> Unit
                }
            }
        }
    }
}
