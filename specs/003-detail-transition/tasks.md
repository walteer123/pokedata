# Tasks: Pokemon Detail Transition Animation

**Input**: Design documents from `/specs/003-detail-transition/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/interfaces.md

**Tests**: Not explicitly requested in the spec. UI verification via quickstart.md manual testing.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (No story — infrastructure only)

**Purpose**: Establish the shared element foundation that all user stories depend on.

- [ ] T001 Add `spriteUrl: String` and `name: String` parameters to `Route.PokemonDetail` in `core/navigation/src/main/java/com/pokedata/core/navigation/Route.kt`
- [ ] T002 Add `imageModifier: Modifier = Modifier` parameter to `PokemonCard` in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/PokemonCard.kt`
- [ ] T003 Apply `imageModifier` to the `AsyncImage` inside `PokemonCard` (after `.size(80.dp)`) in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/PokemonCard.kt`
- [ ] T004 Wrap `AppNavHost` in `SharedTransitionLayout` inside `PokedexNavHost` in `app/src/main/java/com/pokedata/PokedexNavHost.kt`
- [ ] T005 Add `spriteUrl: String = ""` and `pokemonName: String = ""` parameters to `PokemonDetailScreen` in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T006 Pass `spriteUrl` and `pokemonName` from route to `PokemonDetailScreen` in `PokedexNavHost` composable lambda for `Route.PokemonDetail` in `app/src/main/java/com/pokedata/PokedexNavHost.kt`

**Checkpoint**: Foundation in place. PokemonCard accepts imageModifier, Route carries spriteUrl/name, SharedTransitionLayout wraps NavHost, detail screen accepts new params.

---

## Phase 2: User Story 1 — Animated shared element transition on Pokemon image (Priority: P1) 🎯 MVP

**Goal**: When a user taps a Pokemon card, the sprite image smoothly expands and moves to the artwork position on the detail screen. Remaining detail content fades in around it.

**Independent Test**: Tap any Pokemon card in the list; the image should animate from the card position to the detail artwork position with a smooth shared element effect.

### Implementation for User Story 1

- [ ] T007 [US1] Apply `sharedBounds` on the `AsyncImage` artwork in `PokemonDetailContent` composable using `AnimatedContentScope` from the NavHost lambda, in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T008 [US1] Use `spriteUrl` as fallback image model in `PokemonDetailContent` when `artworkUrl` is null (so the shared element shows the correct image during transition), in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T009 [US1] Use `pokemonName` for the immediate top bar title when ViewModel data hasn't loaded yet, in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T010 [US1] Pass `AnimatedContentScope` (via `this`) from `composable<Route.PokemonDetail>` lambda to `PokemonDetailScreen`, in `app/src/main/java/com/pokedata/PokedexNavHost.kt`
- [ ] T011 [US1] In `PokemonListScreen`, pass `spriteUrl` and `name` to `Route.PokemonDetail(pokemonId, spriteUrl, name)` via `onPokemonClick`, in `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`
- [ ] T012 [US1] In `PokemonListScreen`'s `PokemonCard` usage, pass `imageModifier` with `sharedBounds` using `AnimatedContentScope`, in `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`
- [ ] T013 [US1] Replace the slide `enterTransition`/`exitTransition`/`popEnterTransition`/`popExitTransition` on `Route.PokemonDetail` with fade-only transitions (since `sharedBounds` handles the image motion), in `app/src/main/java/com/pokedata/PokedexNavHost.kt`

**Checkpoint**: User Story 1 complete. Tapping a card in the Pokemon List shows a smooth shared element transition on the image to the detail screen.

---

## Phase 3: User Story 2 — Animated return transition when navigating back (Priority: P2)

**Goal**: When the user presses back from the detail screen, the artwork image shrinks and returns to the original card position in the list.

**Independent Test**: Navigate to detail, press back; the artwork should animate back to the card's image position. List scroll position should be preserved.

### Implementation for User Story 2

- [ ] T014 [US2] Ensure `sharedBounds` on the detail `AsyncImage` and the list card `AsyncImage` share the same key (e.g., `"pokemon-image-${pokemonId}"`) so the return transition resolves correctly, in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T015 [US2] Verify the `popEnterTransition`/`popExitTransition` fade-only transitions allow the shared element return to be visible without competing slide animation, in `app/src/main/java/com/pokedata/PokedexNavHost.kt`

**Checkpoint**: User Story 2 complete. Back navigation shows the image returning to the card position.

---

## Phase 4: User Story 3 — Smooth non-image content transitions (Priority: P3)

**Goal**: Non-image content (card text, detail stats, types, description) animates smoothly via fade transitions to avoid abrupt visual jumps during forward and back navigation.

**Independent Test**: Observe non-image elements during navigation; they should fade in/out smoothly rather than appearing/disappearing abruptly.

### Implementation for User Story 3

- [ ] T016 [US3] Add `contentBounds` (sharedBounds) to the detail screen's non-image content container (Column wrapping stats/types/description) so it fades in/out smoothly during the transition, in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [ ] T017 [US3] Ensure the `PokemonCard` text content (name, number) fades out when the card is tapped and the detail opens, by relying on the NavHost fade transition on `Route.PokemonDetail`, in `app/src/main/java/com/pokedata/PokedexNavHost.kt`

**Checkpoint**: User Story 3 complete. All content transitions smoothly — image via sharedBounds, text via fade.

---

## Phase 5: Search & Favorites screens — Extend shared element to all entry points (FR-008)

**Goal**: The same shared element animation works from the Search and Favorites screens, since they reuse `PokemonCard`.

**Independent Test**: Tap a Pokemon card in Search or Favorites; the same shared element animation should play as from the List.

### Implementation

- [ ] T018 [US1] In `SearchScreen`, pass `spriteUrl` and `name` to `Route.PokemonDetail(pokemonId, spriteUrl, name)` via `onPokemonClick`, in `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`
- [ ] T019 [US1] In `SearchScreen`'s `PokemonCard` usage, pass `imageModifier` with `sharedBounds` using `AnimatedContentScope`, in `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`
- [ ] T020 [US1] In `FavoritesScreen`, pass `spriteUrl` and `name` to `Route.PokemonDetail(pokemonId, spriteUrl, name)` via `onPokemonClick`, in `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt`
- [ ] T021 [US1] In `FavoritesScreen`'s `PokemonCard` usage, pass `imageModifier` with `sharedBounds` using `AnimatedContentScope`, in `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt`

**Checkpoint**: Shared element animation works from all 3 source screens (List, Search, Favorites).

---

## Phase 6: Polish & Verification

**Purpose**: Final validation and cleanup.

- [ ] T022 Build the project and verify no compilation errors with `./gradlew assembleDebug`
- [ ] T023 Follow quickstart.md manual verification steps on device/emulator
- [ ] T024 Verify rapid-tap edge case — tapping two cards quickly should not crash or produce visual glitches
- [ ] T025 Verify rotation during transition — animation should complete or restart cleanly

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately
- **Phase 2 (US1 — MVP)**: Depends on Phase 1 completion
- **Phase 3 (US2)**: Depends on Phase 1 + Phase 2 completion (shared bounds must be in place)
- **Phase 4 (US3)**: Depends on Phase 1 + Phase 2 completion
- **Phase 5 (Search & Favorites)**: Depends on Phase 1 + Phase 2 completion
- **Phase 6 (Polish)**: Depends on all desired phases being complete

### Within Each Phase

- Phase 1 tasks T001-T002 can run in parallel (different files)
- Phase 1 tasks T003-T006 are sequential (depend on T001-T002)
- Phase 2 tasks T007-T010 can partially parallelize (different files for T007/T011/T012)
- Phase 5 tasks T018-T021 can all run in parallel (different files, same pattern)

### Parallel Opportunities

```text
# Phase 1 — after T001 and T002:
T003 + T006 can run in parallel (PokemonCard.kt vs PokedexNavHost.kt)

# Phase 2 — core wiring:
T007 + T011 + T012 can run in parallel (DetailScreen.kt vs ListScreen.kt)

# Phase 5 — all 4 tasks in parallel:
T018 + T019 + T020 + T21 (SearchScreen.kt + FavoritesScreen.kt)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T006)
2. Complete Phase 2: US1 (T007-T013)
3. **STOP and VALIDATE**: Build, install on device, tap a card, verify shared element animation
4. Deploy/demo if ready

### Incremental Delivery

1. Phase 1 → Phase 2 (US1) → validate on device
2. Add Phase 3 (US2) → validate back navigation
3. Add Phase 4 (US3) → validate fade transitions
4. Add Phase 5 (Search & Favorites) → validate from all screens
5. Phase 6 polish → final sign-off

### Total Task Count

| Phase | Tasks | Story |
|-------|-------|-------|
| Phase 1: Setup | 6 | — |
| Phase 2: US1 (MVP) | 7 | US1 |
| Phase 3: US2 | 2 | US2 |
| Phase 4: US3 | 2 | US3 |
| Phase 5: Search & Favorites | 4 | US1 |
| Phase 6: Polish | 4 | — |
| **Total** | **25** | |

**Suggested MVP scope**: Phase 1 + Phase 2 (13 tasks) — delivers the core shared element animation from the Pokemon List to the detail screen.
