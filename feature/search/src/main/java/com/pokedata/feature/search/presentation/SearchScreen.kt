package com.pokedata.feature.search.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.designsystem.components.EmptyState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.PokemonCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onPokemonClick: (Int, String, String) -> Unit,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: SearchViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    with(sharedTransitionScope) {
                        TextField(
                            value = uiState.query,
                            onValueChange = viewModel::onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .focusRequester(focusRequester)
                                .sharedBounds(
                                    rememberSharedContentState(key = "search-bar"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            placeholder = { Text("Search Pokemon...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (uiState.query.isNotBlank()) {
                                    IconButton(onClick = viewModel::clearSearch) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                errorIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                uiState.hasNoResults -> EmptyState(
                    title = "No results found",
                    subtitle = "Try a different search term"
                )
                uiState.results.isEmpty() && uiState.query.isBlank() -> EmptyState(
                    title = "Search Pokemon",
                    subtitle = "Type a name or number to search"
                )
                else -> {
                    LazyColumn {
                        items(uiState.results.size) { index ->
                            val pokemon = uiState.results[index]
                            with(sharedTransitionScope) {
                                PokemonCard(
                                    id = pokemon.id,
                                    name = pokemon.name,
                                    number = pokemon.id,
                                    spriteUrl = pokemon.spriteUrl,
                                    isFavorite = pokemon.isFavorite,
                                    onClick = { onPokemonClick(pokemon.id, pokemon.spriteUrl ?: "", pokemon.name) },
                                    onFavoriteToggle = null,
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
