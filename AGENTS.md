# pokedata Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-04-22

## Active Technologies
- Kotlin 2.0.21 + Jetpack Compose (BOM 2025.03.01), Material 3, Koin 3.5.6, Navigation Compose 2.9.0, Coil 2.7.0, Paging 3 (002-update-design-system)
- No changes — Room database unchanged (002-update-design-system)
- Kotlin 2.0.21, Compose BOM 2025.03.01 (Compose Animation 1.7.x, Compose UI 1.7.x) + Jetpack Compose, Navigation Compose 2.9.0, Material 3 1.4.0, Coil 2.7.0, Koin 3.5.6 (003-detail-transition)
- Room (unchanged — no data model changes) (003-detail-transition)
- Compose Shared Element Transitions (sharedBounds), slideInHorizontally/slideOutHorizontally, FocusRequester auto-focus (004-search-bar-animation)
- Kotlin (latest stable) + Jetpack Compose, Koin, Retrofit, Room, Coil, Paging 3, Navigation Compose (main)
- Room (local SQLite database) (main)

- Kotlin 2.0+ + Jetpack Compose (BOM 2024+), Koin 3.5+, Retrofit 2.9+, Room 2.6+, Coil 2.6+, Paging 3, Navigation Compose 1.7+, OkHttp 4.12+, kotlinx.coroutines 1.8+, kotlinx.serialization (001-pokedex-app)

## Project Structure

```text
src/
tests/
```

## Commands

# Add commands for Kotlin 2.0+

## Code Style

Kotlin 2.0+: Follow standard conventions

## Recent Changes
- main: Added Kotlin (latest stable) + Jetpack Compose, Koin, Retrofit, Room, Coil, Paging 3, Navigation Compose
- 004-search-bar-animation: Added `SearchBarCompact` component with sharedBounds transition, auto-focus on SearchScreen, slide/fade navigation transitions for Search route
- 003-detail-transition: Added Kotlin 2.0.21, Compose BOM 2025.03.01 (Compose Animation 1.7.x, Compose UI 1.7.x) + Jetpack Compose, Navigation Compose 2.9.0, Material 3 1.4.0, Coil 2.7.0, Koin 3.5.6


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
