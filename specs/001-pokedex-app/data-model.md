# Data Model: Pokedex App

## Entities

### Pokemon

Represents a single Pokemon with all displayable attributes.

**Fields**:
- `id: Int` — National Pokedex number (primary key)
- `name: String` — Pokemon name (lowercase from API, display capitalized)
- `height: Int` — Height in decimeters
- `weight: Int` — Weight in hectograms
- `spriteUrl: String` — URL to the default sprite image
- `description: String` — Flavor text description (English)
- `isFavorite: Boolean` — Whether the user has favorited this Pokemon
- `lastUpdated: Long` — Timestamp of last data refresh (epoch millis)

**Relationships**:
- Has many `PokemonType` (via `pokemon_id`)
- Has many `Ability` (via `pokemon_id`)

**Validation**:
- `name` must not be empty
- `id` must be positive
- `spriteUrl` must be a valid URL (may be empty for missing sprites)

---

### PokemonType

Represents a Pokemon's type (e.g., Fire, Water, Electric).

**Fields**:
- `id: Int` — Auto-generated primary key
- `pokemonId: Int` — Foreign key to Pokemon
- `typeName: String` — Type name (e.g., "fire", "water")
- `slot: Int` — Type slot (1 or 2, for primary/secondary type)

**Validation**:
- `typeName` must not be empty
- `slot` must be 1 or 2

---

### Ability

Represents a Pokemon's ability.

**Fields**:
- `id: Int` — Auto-generated primary key
- `pokemonId: Int` — Foreign key to Pokemon
- `abilityName: String` — Ability name (e.g., "overgrow", "blaze")
- `isHidden: Boolean` — Whether this is a hidden ability

**Validation**:
- `abilityName` must not be empty

---

### BaseStats

Represents a Pokemon's base combat stats. Stored as a single embedded object within Pokemon queries.

**Fields**:
- `pokemonId: Int` — Foreign key to Pokemon (also primary key)
- `hp: Int` — Hit Points
- `attack: Int` — Attack stat
- `defense: Int` — Defense stat
- `specialAttack: Int` — Special Attack stat
- `specialDefense: Int` — Special Defense stat
- `speed: Int` — Speed stat

**Validation**:
- All stats must be between 1 and 255

---

## DTOs (API Response Mapping)

### PokemonListResponse

API response for the paginated Pokemon list endpoint.

**Fields**:
- `count: Int` — Total number of Pokemon available
- `next: String?` — URL to next page (null if last page)
- `previous: String?` — URL to previous page (null if first page)
- `results: List<PokemonSummary>` — List of Pokemon summaries

### PokemonSummary

Minimal Pokemon data for list items.

**Fields**:
- `name: String` — Pokemon name
- `url: String` — API URL for this Pokemon (contains ID)

### PokemonDetailResponse

API response for individual Pokemon detail.

**Fields**:
- `id: Int` — Pokedex number
- `name: String` — Pokemon name
- `height: Int`
- `weight: Int`
- `sprites: SpriteObject` — Contains `front_default` sprite URL
- `stats: List<StatEntry>` — Base stats
- `types: List<TypeEntry>` — Pokemon types
- `abilities: List<AbilityEntry>` — Pokemon abilities
- `species: SpeciesReference` — URL to species endpoint (for description)

### SpeciesDescriptionResponse

API response for Pokemon species (flavor text).

**Fields**:
- `flavor_text_entries: List<FlavorTextEntry>` — Flavor texts in various languages

---

## State Models

### UiState<T>

Generic UI state wrapper emitted by ViewModels.

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}
```

### PokemonListUiState

Specific state for the Pokemon list screen.

```kotlin
data class PokemonListUiState(
    val isLoading: Boolean = false,
    val pokemon: List<PokemonListItem> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class PokemonListItem(
    val id: Int,
    val name: String,
    val spriteUrl: String?,
    val isFavorite: Boolean
)
```

### SearchUiState

State for the search screen.

```kotlin
data class SearchUiState(
    val query: String = "",
    val results: List<PokemonListItem> = emptyList(),
    val isLoading: Boolean = false,
    val hasNoResults: Boolean = false
)
```

---

## Relationships Diagram

```
Pokemon (1) ──┬── (M) PokemonType
              ├── (M) Ability
              └── (1) BaseStats (embedded)

Favorites: Filtered view of Pokemon where isFavorite = true
Search: Filtered query on Pokemon.name or Pokemon.id
```

---

## Room Schema Summary

| Table | Primary Key | Indexed Columns | Foreign Keys |
|-------|------------|-----------------|--------------|
| `pokemon` | `id` | `name`, `isFavorite` | — |
| `pokemon_types` | `id` | `pokemonId` | `pokemonId → pokemon.id` |
| `abilities` | `id` | `pokemonId` | `pokemonId → pokemon.id` |
| `base_stats` | `pokemonId` | — | `pokemonId → pokemon.id` |
