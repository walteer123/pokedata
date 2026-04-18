package com.pokedata.feature.search.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokedata.core.data.model.PokemonListItem
import com.pokedata.core.designsystem.components.EmptyState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.PokemonCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onPokemonClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search Pokemon...") },
                        trailingIcon = {
                            if (uiState.query.isNotBlank()) {
                                IconButton(onClick = viewModel::clearSearch) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = MaterialTheme.shapes.small
                    )
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
                            PokemonCard(
                                id = pokemon.id,
                                name = pokemon.name,
                                number = pokemon.id,
                                spriteUrl = pokemon.spriteUrl,
                                isFavorite = pokemon.isFavorite,
                                onClick = onPokemonClick,
                                onFavoriteToggle = null,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
