package com.pokedata.feature.pokemondetail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pokedata.core.designsystem.components.ErrorState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.TypeBadge
import com.pokedata.core.ui.extensions.capitalizeFirst
import com.pokedata.core.ui.extensions.formatHeight
import com.pokedata.core.ui.extensions.formatWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    modifier: Modifier = Modifier,
    pokemonId: Int,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    viewModel: PokemonDetailViewModel = org.koin.androidx.compose.koinViewModel(parameters = { org.koin.core.parameter.parametersOf(pokemonId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    uiState.pokemon?.let { pokemon ->
                        Text(
                            text = pokemon.name.capitalizeFirst(),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    } ?: Text(
                        text = "Pokemon Detail",
                        style = MaterialTheme.typography.titleLarge
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
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.toggleFavorite()
                        onFavoriteToggle()
                    }) {
                        val isFavorite = uiState.pokemon?.isFavorite == true
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.reload() }
            )
            uiState.pokemon != null -> PokemonDetailContent(uiState.pokemon!!, paddingValues)
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemon: com.pokedata.core.data.model.PokemonDetail,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Artwork
        AsyncImage(
            model = pokemon.artworkUrl ?: pokemon.spriteUrl,
            contentDescription = pokemon.name,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Types
        if (pokemon.types.isNotEmpty()) {
            Text(
                text = "Types",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pokemon.types.forEach { type ->
                    TypeBadge(typeName = type.name)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = pokemon.height.formatHeight(), style = MaterialTheme.typography.bodyLarge)
                Text(text = "Height", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = pokemon.weight.formatWeight(), style = MaterialTheme.typography.bodyLarge)
                Text(text = "Weight", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        val description = pokemon.description
        if (!description.isNullOrEmpty()) {
            Text(text = "About", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Abilities
        if (pokemon.abilities.isNotEmpty()) {
            Text(text = "Abilities", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            pokemon.abilities.forEach { ability ->
                Text(
                    text = ability.name.capitalizeFirst() + if (ability.isHidden) " (Hidden)" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats
        if (pokemon.stats.isNotEmpty()) {
            Text(text = "Base Stats", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            pokemon.stats.forEach { (statName, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statName.capitalizeFirst(),
                        modifier = Modifier.weight(0.4f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$value",
                        modifier = Modifier.weight(0.15f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(
                        progress = { value / 255f },
                        modifier = Modifier.weight(0.45f).height(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
