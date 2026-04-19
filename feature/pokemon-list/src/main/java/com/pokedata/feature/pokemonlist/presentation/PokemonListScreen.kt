package com.pokedata.feature.pokemonlist.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.pokedata.core.designsystem.components.TypeFilterChips
import com.pokedata.feature.pokemonlist.presentation.PokemonTypes

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
                pokemon.loadState.refresh is LoadState.Loading && pokemon.itemCount == 0 -> {
                    LoadingIndicator()
                }
                pokemon.loadState.refresh is LoadState.Error && pokemon.itemCount == 0 -> {
                    val error = (pokemon.loadState.refresh as LoadState.Error).error
                    ErrorState(
                        message = error.message ?: "Failed to load Pokemon",
                        onRetry = { pokemon.refresh() }
                    )
                }
                else -> {
                    Column {
                        TypeFilterChips(
                            types = PokemonTypes,
                            selectedType = uiState.selectedTypeFilter,
                            onTypeSelected = viewModel::setTypeFilter,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        PokemonListContent(
                            pokemon = pokemon,
                            isRefreshing = pokemon.loadState.refresh is LoadState.Loading,
                            onRefresh = { pokemon.refresh() },
                            onPokemonClick = onPokemonClick,
                            onFavoriteToggle = viewModel::toggleFavorite,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }

            if (pokemon.loadState.refresh is LoadState.Error && pokemon.itemCount > 0) {
                val error = (pokemon.loadState.refresh as LoadState.Error).error
                Text(
                    text = error.message ?: "Failed to load Pokemon",
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
                            types = pokemonItem.types,
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

            // Mostrar indicador de carregamento no final da lista quando estiver carregando mais
            item {
                when (val appendState = pokemon.loadState.append) {
                    is LoadState.Loading -> {
                        LoadingIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is LoadState.Error -> {
                        // Mostrar mensagem de erro e botão de retry
                        androidx.compose.material3.Text(
                            text = "Error loading more: ${(appendState.error).message}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        // Verificar se há mais itens para carregar
                        if (pokemon.itemCount > 0 && pokemon.loadState.append is LoadState.NotLoading) {
                            // Se não estamos no fim da paginação, mostrar um spacer para garantir
                            // que o usuário possa continuar scrollando
                            androidx.compose.foundation.layout.Spacer(
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
