# Data Model: Shared Element Transitions

**Date**: 2026-04-18
**Feature**: 003-detail-transition

## Entities Modified

### Route.PokemonDetail (Modified)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| pokemonId | Int | Yes | Existing field. Unique Pokemon identifier. |
| spriteUrl | String | Yes | NEW. Small sprite URL displayed in the card. Used as immediate placeholder in detail screen during shared element transition. |
| name | String | Yes | NEW. Pokemon display name. Used for instant title rendering in detail screen top bar before ViewModel data loads. |

**Validation**: `spriteUrl` must be a valid URL string. `name` must be non-empty.
**Backward compatibility**: Existing navigation calls that only pass `pokemonId` will need to be updated. Default empty-string values are provided to prevent crashes during migration, but all call sites should be updated.

### PokemonCard (Modified — API surface)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| id | Int | required | Existing. |
| name | String | required | Existing. |
| number | Int | required | Existing. |
| spriteUrl | String? | required | Existing. |
| isFavorite | Boolean | required | Existing. |
| onClick | (Int) -> Unit | required | Existing. |
| onFavoriteToggle | ((Int) -> Unit)? | null | Existing. |
| modifier | Modifier | Modifier | Existing. Applied to the Card wrapper. |
| imageModifier | Modifier | Modifier | **NEW**. Applied specifically to the AsyncImage. Used by callers to attach shared element modifiers. |

### PokemonDetailScreen (Modified — composable signature)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| modifier | Modifier | Modifier | Existing. |
| pokemonId | Int | required | Existing. |
| spriteUrl | String | "" | **NEW**. Sprite URL passed from route for immediate rendering during transition. |
| pokemonName | String | "" | **NEW**. Name passed from route for immediate title rendering. |
| onBackClick | () -> Unit | required | Existing. |
| onFavoriteToggle | () -> Unit | required | Existing. |
| viewModel | PokemonDetailViewModel | koinViewModel | Existing. |

## State Transitions

No new state machines. The shared element animation is driven by navigation state (entering/exiting the detail route) and does not introduce new ViewModel states.

## Relationships

```
Route.PokemonDetail
  ├─ pokemonId → ViewModel parameter → data layer fetch
  ├─ spriteUrl → PokemonDetailScreen → AsyncImage (placeholder)
  └─ name → PokemonDetailScreen → TopAppBar title

PokemonCard (core:designsystem)
  └─ imageModifier → AsyncImage (shared element modifier applied by caller)

PokemonDetailContent
  └─ AsyncImage (artworkUrl) → shared bounds with PokemonCard.AsyncImage
```
