# Research: Pokedex App

## Decision: API Source

**Chosen**: PokeAPI (https://pokeapi.co/api/v2/)

**Rationale**: PokeAPI is the most comprehensive, free, and well-documented Pokemon API. It provides all required data: Pokemon list (paginated), individual Pokemon details (types, abilities, stats, sprites), and type information. No authentication required, stable uptime, and widely used in the developer community.

**Alternatives considered**:
- Pokemon TCG API — focused on cards, not Pokedex data
- Custom backend — unnecessary complexity for a data consumption app
- GraphQL Pokemon API — less common, harder to integrate with Retrofit

---

## Decision: Pagination Strategy

**Chosen**: Paging 3 with RemoteMediator (network + Room integration)

**Rationale**: PokeAPI returns paginated results (offset/limit). RemoteMediator allows seamless integration between network pagination and Room caching. When the user scrolls, Paging 3 fetches the next page from the API and stores it in Room, which then emits to the UI. This satisfies the offline-first requirement — previously loaded pages are available from Room without network.

**Alternatives considered**:
- Manual pagination with offset tracking — more boilerplate, no built-in loading state management
- Load all at once — impractical for 1000+ Pokemon, poor performance
- Cursor-based pagination — PokeAPI uses offset/limit, not cursors

---

## Decision: Image Loading

**Chosen**: Coil 2.6+ with `AsyncImage`

**Rationale**: Coil is the standard image loading library for Jetpack Compose. It supports coroutines natively, has built-in memory and disk caching, and integrates seamlessly with Compose. PokeAPI provides sprite URLs which Coil can load efficiently.

**Alternatives considered**:
- Glide with Compose interop — requires additional wrapper library, less native Compose support
- Picasso — older, no native Compose support
- Manual OkHttp + Bitmap — unnecessary complexity

---

## Decision: State Management Pattern

**Chosen**: `StateFlow<UiState<T>>` in ViewModels, consumed via `collectAsStateWithLifecycle()` in Composables

**Rationale**: `StateFlow` is lifecycle-aware, conflates values (no duplicate emissions), and integrates naturally with Compose. `collectAsStateWithLifecycle()` ensures collection is paused when the composable is not visible, saving resources. The `UiState` sealed class (`Loading`, `Success`, `Error`) provides exhaustive state handling.

**Alternatives considered**:
- `SharedFlow` — better for one-time events, not ideal for UI state
- `LiveData` — legacy, not native to Compose, requires conversion
- Compose `mutableStateOf` in ViewModel — no lifecycle awareness, harder to test

---

## Decision: Dependency Injection

**Chosen**: Koin 3.5+

**Rationale**: Koin is lightweight, uses Kotlin DSL for module definitions, has no code generation overhead (unlike Hilt/Dagger), and integrates well with Compose and ViewModel. The constitution mandates Koin.

**Alternatives considered**:
- Hilt/Dagger — more boilerplate, compile-time code generation, heavier setup
- Manual DI — unmanageable at this scale

---

## Decision: Navigation Pattern

**Chosen**: Navigation Compose with sealed class routes

**Rationale**: Type-safe navigation using sealed classes prevents runtime errors from malformed route strings. Navigation Compose integrates natively with Compose UI and supports deep links, back stack management, and animations.

**Route structure**:
```kotlin
sealed class Route(val route: String) {
    object PokemonList : Route("pokemon_list")
    object PokemonDetail : Route("pokemon_detail/{pokemonId}") {
        fun createRoute(pokemonId: Int) = "pokemon_detail/$pokemonId"
    }
    object Search : Route("search")
    object Favorites : Route("favorites")
}
```

**Alternatives considered**:
- String-based routes — error-prone, no compile-time safety
- Voyager/Decompose — third-party, less ecosystem support

---

## Decision: Room Schema Design

**Chosen**: Separate entities for `PokemonEntity`, `PokemonTypeEntity`, `AbilityEntity`, with `@Relation` for complex queries

**Rationale**: Normalized schema avoids data duplication. `@Relation` allows fetching Pokemon with its types and abilities in a single query. Favorites are tracked via a boolean flag on `PokemonEntity` rather than a separate table, simplifying queries.

**Alternatives considered**:
- Single denormalized table — simpler queries but data duplication, harder to maintain
- Separate favorites table — more complex queries for "list favorites"

---

## Decision: Search Implementation

**Chosen**: Client-side search on cached Room data with debounced input

**Rationale**: Searching locally in Room is fast and works offline. Debouncing (300ms) prevents excessive queries while typing. For initial load, the full Pokemon list is fetched and cached, enabling instant search. This avoids network calls for every keystroke.

**Alternatives considered**:
- Server-side search via API — requires network, slower, doesn't work offline
- Full-text search (FTS4/FTS5) — overkill for ~1000 Pokemon names, simple LIKE query is sufficient

---

## Decision: Error Handling Strategy

**Chosen**: Sealed class `Result<T>` with `Success`, `Error`, and `Loading` states propagated from Repository through ViewModel to UI

**Rationale**: Explicit error types allow composables to render appropriate UI (retry button, error message, cached data indicator). Network errors are distinguished from database errors. The UI can show stale data with a snackbar when offline.

**Alternatives considered**:
- Exception throwing — forces try/catch everywhere, harder to compose
- Nullable returns — loses error context
