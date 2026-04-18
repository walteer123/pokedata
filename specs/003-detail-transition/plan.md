# Implementation Plan: Pokemon Detail Transition Animation

**Branch**: `003-detail-transition` | **Date**: 2026-04-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-detail-transition/spec.md`

## Summary

Implement shared element transitions using the Compose Shared Element Transitions API (`SharedTransitionLayout` + `sharedBounds`) to animate the Pokemon image from the card in the list/search/favorites screens to the artwork position on the detail screen, and back. The `PokemonCard` composable gains an `imageModifier` parameter; the `Route.PokemonDetail` route gains `spriteUrl` and `name` parameters for immediate rendering; and `PokedexNavHost` wraps its content in `SharedTransitionLayout`.

## Technical Context

**Language/Version**: Kotlin 2.0.21, Compose BOM 2025.03.01 (Compose Animation 1.7.x, Compose UI 1.7.x)
**Primary Dependencies**: Jetpack Compose, Navigation Compose 2.9.0, Material 3 1.4.0, Coil 2.7.0, Koin 3.5.6
**Storage**: Room (unchanged — no data model changes)
**Testing**: Compose Testing API, JUnit
**Target Platform**: Android (minSdk per project config)
**Project Type**: Android modular app (feature modules + core modules)
**Performance Goals**: Shared element transition completes in < 500ms; no frame drops during animation
**Constraints**: Must work across 3 source screens (List, Search, Favorites) using the same `PokemonCard`; images may differ between card (sprite) and detail (artwork)
**Scale/Scope**: 4 files modified, 0 new modules, ~80 lines of net new code

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Feature-First Modularity**: No new modules. Changes live in existing modules: `core:designsystem` (PokemonCard param), `core:navigation` (Route params), `feature:pokemon-detail` (screen params), `app` (SharedTransitionLayout wrapper). No cross-feature dependencies introduced.
- [x] **MVVM + Clean Architecture**: No changes to ViewModel, UseCase, or Repository layers. The animation is purely a Presentation-layer concern. ViewModel remains unchanged.
- [x] **Compose-First UI**: All changes are Compose composables and Navigation Compose. No XML layouts, no View-system APIs.
- [x] **Test-Guided Development**: UI tests can verify shared element transition renders correctly. Unit tests are not applicable (no business logic changed). Compose Testing API can assert that `PokemonCard` and detail screen render with shared element modifiers without crashing.
- [x] **Offline-First Data**: No data layer changes. The `spriteUrl` passed in the route is already cached data from the list.
- [x] **Material 3 Design System**: Uses M3 components unchanged. `sharedBounds` uses M3 default animation specs (300ms). No custom M3 theme changes.
- [x] **Performance & Memory**: Shared element animation is GPU-accelerated. No additional image loading (spriteUrl is reused from the list). Coil handles image caching. Transition completes in < 500ms per spec.

## Project Structure

### Documentation (this feature)

```text
specs/003-detail-transition/
├── plan.md              # This file
├── research.md          # Phase 0: architectural decisions
├── data-model.md        # Phase 1: entity modifications
├── quickstart.md        # Phase 1: manual verification steps
├── contracts/           # Phase 1: interface contracts
│   └── interfaces.md
└── tasks.md             # Phase 2: NOT created by /speckit.plan
```

### Files to modify (source code)

```text
core/designsystem/.../components/PokemonCard.kt    # Add imageModifier param
core/navigation/.../Route.kt                       # Add spriteUrl, name to PokemonDetail
feature/pokemon-detail/.../presentation/PokemonDetailScreen.kt  # Accept spriteUrl, pokemonName; apply sharedBounds
app/.../PokedexNavHost.kt                          # Wrap in SharedTransitionLayout; pass route params from 3 screens
```

### Files NOT modified

```text
core/navigation/.../AppNavHost.kt          # Stays as thin NavHost wrapper
feature/pokemon-list/.../ListScreen.kt     # Only change: pass spriteUrl/name to route
feature/search/.../SearchScreen.kt         # Only change: pass spriteUrl/name to route
feature/favorites/.../FavoritesScreen.kt   # Only change: pass spriteUrl/name to route
```

## Complexity Tracking

No constitution violations. All gates pass.

## Phase 0: Research — Complete

See [research.md](./research.md). Key decisions:
- Use `sharedBounds` (not `sharedElement`) because card and detail show different image URLs
- Add `spriteUrl` and `name` to `Route.PokemonDetail` for instant rendering
- Add `imageModifier` to `PokemonCard` to keep design system module agnostic
- Place `SharedTransitionLayout` in `PokedexNavHost.kt`
- Scope: image-only shared element (name/stats animated via existing slide/fade)

## Phase 1: Design — Complete

See [data-model.md](./data-model.md), [contracts/interfaces.md](./contracts/interfaces.md), [quickstart.md](./quickstart.md).

### Implementation Order

1. **Route.kt** — Add `spriteUrl: String` and `name: String` to `PokemonDetail`
2. **PokemonCard.kt** — Add `imageModifier: Modifier = Modifier`, apply after `.size(80.dp)` on `AsyncImage`
3. **PokemonDetailScreen.kt** — Accept `spriteUrl` and `pokemonName` params; use `spriteUrl` as fallback while ViewModel loads; apply `sharedBounds` on artwork `AsyncImage`; pass `AnimatedContentScope` from NavHost lambda
4. **PokedexNavHost.kt** — Wrap `AppNavHost` in `SharedTransitionLayout`; apply `sharedBounds` on card image via `imageModifier` in each source screen; replace slide transitions on detail route with fade-only transitions (shared bounds handles the image motion); pass `spriteUrl` and `name` in `Route.PokemonDetail` constructor at all 3 navigation call sites
