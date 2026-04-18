# Interface Contracts: Shared Element Transitions

**Date**: 2026-04-18
**Feature**: 003-detail-transition

This feature is purely internal UI — no external APIs, CLI interfaces, or service endpoints are created or modified. The contracts below define the component interfaces that will be changed.

## Contract 1: PokemonCard — imageModifier parameter

**Module**: `core:designsystem`
**File**: `PokemonCard.kt`

```kotlin
@Composable
fun PokemonCard(
    id: Int,
    name: String,
    number: Int,
    spriteUrl: String?,
    isFavorite: Boolean,
    onClick: (Int) -> Unit,
    onFavoriteToggle: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier  // NEW — applied to AsyncImage
)
```

**Semantics**: The `imageModifier` is applied after the existing `.size(80.dp)` on the `AsyncImage`. Callers can pass shared element modifiers (e.g., `sharedBounds(...)`) through this parameter without modifying `PokemonCard` internals.

## Contract 2: Route.PokemonDetail — enriched parameters

**Module**: `core:navigation`
**File**: `Route.kt`

```kotlin
@Serializable
data class PokemonDetail(
    val pokemonId: Int,
    val spriteUrl: String = "",
    val name: String = ""
) : Route()
```

**Semantics**: `spriteUrl` and `name` are passed from the source screen at navigation time. They allow the detail screen to render immediately before the ViewModel loads full data. Default values prevent crashes if a call site forgets to pass them during migration.

## Contract 3: PokemonDetailScreen — spriteUrl and pokemonName params

**Module**: `feature:pokemon-detail`
**File**: `PokemonDetailScreen.kt`

```kotlin
@Composable
fun PokemonDetailScreen(
    modifier: Modifier = Modifier,
    pokemonId: Int,
    spriteUrl: String = "",
    pokemonName: String = "",
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    viewModel: PokemonDetailViewModel = koinViewModel(...)
)
```

**Semantics**: `spriteUrl` is used as the initial image source in the shared element transition before the ViewModel loads `artworkUrl`. `pokemonName` is used for the immediate top bar title.

## Contract 4: PokedexNavHost — SharedTransitionLayout wrapper

**Module**: `app`
**File**: `PokedexNavHost.kt`

The `PokedexNavHost` composable will wrap its `AppNavHost` content in `SharedTransitionLayout` and pass `AnimatedContentScope` (via `this` in the composable lambda) to the detail screen. The calling pattern from list/search/favorites screens will pass `spriteUrl` and `name` into the `Route.PokemonDetail` constructor.
