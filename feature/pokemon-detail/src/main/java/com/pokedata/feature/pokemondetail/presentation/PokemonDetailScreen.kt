package com.pokedata.feature.pokemondetail.presentation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pokedata.core.designsystem.components.ErrorState
import com.pokedata.core.designsystem.components.LoadingIndicator
import com.pokedata.core.designsystem.components.TypeBadge
import com.pokedata.core.ui.extensions.capitalizeFirst
import com.pokedata.core.ui.extensions.formatHeight
import com.pokedata.core.ui.extensions.formatWeight
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonDetailScreen(
    modifier: Modifier = Modifier,
    pokemonId: Int,
    spriteUrl: String = "",
    pokemonName: String = "",
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    isInPane: Boolean = false,
    viewModel: PokemonDetailViewModel = org.koin.androidx.compose.koinViewModel(parameters = { org.koin.core.parameter.parametersOf(pokemonId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    val titleText = uiState.pokemon?.name ?: pokemonName
                    if (titleText.isNotEmpty()) {
                        Text(
                            text = titleText.capitalizeFirst(),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "Pokemon Detail",
                            style = MaterialTheme.typography.titleLarge
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
            uiState.pokemon != null -> PokemonDetailContent(
                pokemon = uiState.pokemon!!,
                spriteUrl = spriteUrl,
                paddingValues = paddingValues,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                isWideLayout = windowWidthSizeClass != WindowWidthSizeClass.Compact && !isInPane
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonDetailContent(
    pokemon: com.pokedata.core.data.model.PokemonDetail,
    spriteUrl: String,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    isWideLayout: Boolean = false,
    modifier: Modifier = Modifier
) {
    val imageMod = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                rememberSharedContentState(key = "pokemon-image-${pokemon.id}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    if (isWideLayout) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Left column: artwork, types, info, description
            Column(
                modifier = Modifier.weight(1f).padding(end = 12.dp)
            ) {
                AsyncImage(
                    model = pokemon.artworkUrl ?: spriteUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.CenterHorizontally)
                        .then(imageMod)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (pokemon.types.isNotEmpty()) {
                    Text(text = "Types", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pokemon.types.forEach { type -> TypeBadge(typeName = type.name) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

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

                val description = pokemon.description
                if (!description.isNullOrEmpty()) {
                    Text(text = "About", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Right column: abilities, stats
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp)
            ) {
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

                if (pokemon.stats.isNotEmpty()) {
                    Text(text = "Base Stats", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    pokemon.stats.forEach { (statName, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = statName.capitalizeFirst(), modifier = Modifier.weight(0.35f), style = MaterialTheme.typography.bodyMedium)
                            Text(text = "$value", modifier = Modifier.weight(0.15f), textAlign = TextAlign.End, style = MaterialTheme.typography.bodyMedium)
                            LinearProgressIndicator(
                                progress = { value / 255f },
                                modifier = Modifier.weight(0.5f).height(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AsyncImage(
                model = pokemon.artworkUrl ?: spriteUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .then(imageMod)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (pokemon.types.isNotEmpty()) {
                Text(text = "Types", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.forEach { type -> TypeBadge(typeName = type.name) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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

            val description = pokemon.description
            if (!description.isNullOrEmpty()) {
                Text(text = "About", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
            }

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

            if (pokemon.stats.isNotEmpty()) {
                Text(text = "Base Stats", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                pokemon.stats.forEach { (statName, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = statName.capitalizeFirst(), modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium)
                        Text(text = "$value", modifier = Modifier.weight(0.15f), textAlign = TextAlign.End, style = MaterialTheme.typography.bodyMedium)
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
}
