# Tasks: Adaptive Layout (Large Screen, Foldable & Small Screen Support)

**Input**: Design documents from `/specs/005-adaptive-layout/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, quickstart.md

**Tests**: Not explicitly requested in feature spec. No test tasks included. UI verification via quickstart.md manual checklist.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Feature modules: `feature/<name>/src/main/java/com/pokedata/feature/<name>/`
- Core modules: `core/<name>/src/main/java/com/pokedata/core/<name>/`
- App module: `app/src/main/java/com/pokedata/`
- Gradle: `gradle/`, root `build.gradle.kts`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Add new dependencies for foldable/window detection and ensure build stability

- [x] T001 Add `window-core` and `window-runtime-compose` library declarations to `gradle/libs.versions.toml` — add version `window = "1.3.0"` in `[versions]`, and two library entries in `[libraries]`: `androidx-window-core` and `androidx-window-compose`
- [x] T002 [P] Add `implementation(libs.androidx.window.core)` and `implementation(libs.androidx.window.compose)` dependencies to `app/build.gradle.kts`
- [x] T003 [P] Add `implementation(libs.androidx.compose.material3.window.size)` dependency to `core/ui/build.gradle.kts`
- [x] T004 Run `./gradlew assembleDebug` to verify all dependencies resolve successfully

**Checkpoint**: All new dependencies available, build green

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core adaptive infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 [P] Create `AdaptiveUtils.kt` in `core/ui/src/main/java/com/pokedata/core/ui/AdaptiveUtils.kt` — provide `rememberWindowSizeClass()` returning `WindowWidthSizeClass` from `currentWindowAdaptiveInfo()`, and `rememberFoldingFeatures()` returning `List<FoldingFeature>` from `WindowInfoTracker`
- [x] T006 [P] Extract nav item data class (`NavItem` with `icon: ImageVector`, `label: String`, `route: String`) and `navItems` list from `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/ModernBottomNav.kt` — keep the existing `ModernBottomNav` composable functional but expose nav items as a shared data source
- [x] T007 [P] Create `AdaptiveNavSuite.kt` in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/AdaptiveNavSuite.kt` — wrap `NavigationSuiteScaffold` from material3-adaptive, accepting nav items, current route, and `onNavigate` callback; component auto-switches between `NavigationBar`/`NavigationRail` based on window size
- [x] T008 [P] Update `PokemonCard.kt` in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/PokemonCard.kt` — make card height flexible for grid context (remove any `fillMaxWidth` that causes stretched cards, use `Modifier.fillMaxWidth()` + `aspectRatio` or fixed `height` as appropriate for both list and grid containers)
- [x] T009 Wire `WindowSizeClass` into `PokedexNavHost.kt` at `app/src/main/java/com/pokedata/PokedexNavHost.kt` — call `currentWindowAdaptiveInfo()` at the top level and propagate `WindowWidthSizeClass` to screen composables via composable parameters

**Checkpoint**: Foundation ready — window size class flows to all screens, nav items extracted, adaptive nav shell built, PokemonCard grid-ready

---

## Phase 3: User Story 1 - Adaptive Collection Browsing (Priority: P1) 🎯 MVP

**Goal**: Pokemon list, search results, and favorites display in multi-column grid on tablets (>= 600dp) and single-column list on phones (< 600dp)

**Independent Test**: Open Pokemon list, search results, and favorites on a tablet emulator (>= 600dp) — verify items appear in multi-column grid. On a phone (< 600dp), verify single-column list is preserved.

### Implementation for User Story 1

- [x] T010 [P] [US1] Update `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt` — accept `windowWidthSizeClass` parameter; when `>= WindowWidthSizeClass.MEDIUM`, replace `LazyColumn` with `LazyVerticalGrid(columns = GridCells.Adaptive(160.dp))`, preserving all existing `items()` content, `key`, `contentType`, load-state handling, and pull-to-refresh
- [x] T011 [P] [US1] Update `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt` — accept `windowWidthSizeClass` parameter; when `>= MEDIUM`, use `LazyVerticalGrid(cells = GridCells.Adaptive(160.dp))` for search results instead of `LazyColumn`
- [x] T012 [P] [US1] Update `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt` — accept `windowWidthSizeClass` parameter; when `>= MEDIUM`, use `LazyVerticalGrid(cells = GridCells.Adaptive(160.dp))` for favorites list instead of `LazyColumn`

**Checkpoint**: On tablets, Pokemon list, search, and favorites all show multi-column grids. On phones, single-column lists — no change. MVP complete.

---

## Phase 4: User Story 3 - Adaptive Navigation Chrome (Priority: P3)

**Goal**: Navigation rail on tablets (>= 600dp), bottom navigation bar on phones (< 600dp), following Material 3 adaptive navigation patterns

**Independent Test**: Launch app on tablet — navigation rail appears on the side. On phone — bottom navigation bar remains. Tap any rail/bar item to switch screens.

### Implementation for User Story 3

- [x] T013 [US3] Update `app/src/main/java/com/pokedata/PokedexNavHost.kt` — replace the outer `Scaffold` + `ModernBottomNav` with `NavigationSuiteScaffold` (from Phase 2 `AdaptiveNavSuite.kt`), passing the extracted nav items, current route detection, and navController navigation lambdas. Ensure `NavHost` remains inside the scaffold content lambda with `paddingValues` applied.

**Checkpoint**: Navigation component adapts automatically — `NavigationBar` on compact, `NavigationRail` on medium+. All 3 tabs (Pokedex, Search, Favorites) navigate correctly.

---

## Phase 5: User Story 2 - Master-Detail Split Pane (Priority: P2)

**Goal**: On large screens (>= 840dp), Pokemon list and detail appear side-by-side in a two-pane layout. Selecting a Pokemon in the list updates the detail pane inline — no navigation transition.

**Independent Test**: Open app on tablet emulator with width >= 840dp — Pokemon list on left, detail on right. Tap different Pokemon in list — detail pane updates immediately. On phone (< 600dp) — existing push-navigation behavior preserved.

### Implementation for User Story 2

- [x] T014 [P] [US2] Update `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt` — add an `isInPane: Boolean = false` parameter. When `true`, omit NavHost-dependent parameters (e.g., `sharedTransitionScope`, `animatedVisibilityScope`), render as a standalone composable. When `false` (default), existing behavior unchanged. Also accept `spriteUrl: String?` and `pokemonName: String` as explicit params for pane mode (passed directly from list item data to avoid re-fetch).
- [x] T015 [US2] Update `app/src/main/java/com/pokedata/PokedexNavHost.kt` — inside `composable<Route.PokemonList>`, when `windowWidthSizeClass >= EXPANDED`, wrap content in `ListDetailPaneScaffold` with list pane containing `PokemonListScreen` and detail pane containing the in-pane `PokemonDetailScreen` variant. Track selected Pokemon ID with `rememberSaveable`. On `COMPACT`/`MEDIUM`, fall back to existing single-pane behavior (NavHost navigate to `Route.PokemonDetail`). Use `adaptiveLayoutDirective` from `currentWindowAdaptiveInfo()` for scaffold directive.

**Checkpoint**: Split pane works on expanded screens. Compact screens use existing push navigation. Shared element transitions preserved on compact path.

---

## Phase 6: User Story 4 - Enhanced Detail Layout (Priority: P4)

**Goal**: Pokemon detail screen displays content in multi-column layout on large screens (>= 600dp full-width), eliminating excessive vertical scrolling

**Independent Test**: View a Pokemon detail on tablet in full-screen mode — content is two columns (artwork+info left, stats+abilities right). On phone or in split pane — single column.

### Implementation for User Story 4

- [x] T016 [US4] Update `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt` — add `isWideLayout: Boolean` parameter. When `true` AND `isInPane == false` (full-width only), arrange detail content in a two-column `Row`: left column (weight 0.5) = artwork, name, number, types, height/weight, description; right column (weight 0.5) = base stats bars, abilities list, evolution section. When `false` or `isInPane == true`, use existing single-column `Column`. Both columns independently `verticalScroll`.

**Checkpoint**: Detail view on full-screen tablet => two columns. Detail in split pane or on phone => single column.

---

## Phase 7: User Story 5 - Foldable & Posture-Aware Adaptation (Priority: P5)

**Goal**: App respects hinge/seam boundaries on foldable devices, transitions smoothly when folding/unfolding, state preserved across configuration changes

**Independent Test**: Use foldable emulator — fold device to closed, open, tabletop posture. Verify layout adaptation and no content in hinge zones.

### Implementation for User Story 5

- [x] T017 [US5] Update `app/src/main/java/com/pokedata/PokedexNavHost.kt` — add `rememberFoldingFeatures()` call from `AdaptiveUtils.kt`. Determine `hingePadding` when a `FoldingFeature` with `isSeparating == true` or `state == HALF_OPENED` is present. Pass hinge info down to relevant composables.
- [x] T018 [US5] Apply hinge avoidance in `app/src/main/java/com/pokedata/PokedexNavHost.kt` — when `ListDetailPaneScaffold` is active (Expanded), enable `HingePolicy` for automatic book-posture pane separation. For tabletop posture (`HALF_OPENED` + `HORIZONTAL`), constrain content to the top half of the screen by applying `Modifier.padding()` or `Modifier.height()` based on `FoldingFeature.bounds`. For all other screens, add `Modifier.padding(horizontal = hingePadding)` when a vertical hinge is detected to prevent content spanning the seam.

**Checkpoint**: Content avoids hinge on foldable devices in all postures. Layout transitions smoothly on fold/unfold. State preserved.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Verify no regressions, edge cases handled, documentation validation

- [x] T019 Verify shared element transition animation (`sharedBounds`) still works when navigating from Pokemon list to detail on compact screens (< 600dp) — inspect `PokedexNavHost.kt` compact path preserves `SharedTransitionLayout` + NavHost navigation
- [x] T020 [P] Verify all edge cases from `spec.md` section "Edge Cases": rotation from landscape to portrait in split pane, fold/unfold while in split pane, freeform window resize, exact threshold boundaries (599dp ↔ 600dp), very small screens (320dp), active search during config change
- [x] T021 Run manual test checklist from `specs/005-adaptive-layout/quickstart.md` on phone (411dp), tablet portrait (800dp), tablet landscape (1280dp), and foldable emulators

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phases 3-7)**: All depend on Foundational phase completion
  - US1 (Phase 3): No dependency on other stories — can start after Foundational
  - US3 (Phase 4): No dependency on other stories — can start after Foundational
  - US2 (Phase 5): No dependency on other stories, but shares `PokedexNavHost.kt` with US3 — recommended to implement after US3 to avoid merge conflicts
  - US4 (Phase 6): No dependency on other stories — can start after Foundational
  - US5 (Phase 7): Depends on US2 (needs `ListDetailPaneScaffold` for `HingePolicy`) and Setup (needs window deps)
- **Polish (Phase 8)**: Depends on all desired user stories being complete

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational — No dependencies on other stories
- **US3 (P3)**: Can start after Foundational — No dependencies on other stories
- **US2 (P2)**: Can start after Foundational — No hard dependency on US3, but shares `PokedexNavHost.kt` (recommend sequential: US3 then US2)
- **US4 (P4)**: Can start after Foundational — No dependencies on other stories
- **US5 (P5)**: Depends on US2 (`ListDetailPaneScaffold` for hinge policy) + Setup (window deps)

### Within Each User Story

- Parallel tasks marked [P] can execute simultaneously
- Non-parallel tasks execute sequentially within the phase
- Core implementation before integration

### Parallel Opportunities

- All Setup tasks T001-T003 can run in parallel (T004 validates after)
- All Foundational tasks T005-T008 can run in parallel (T009 after)
- All US1 tasks T010-T012 can run in parallel (different feature modules)
- US1, US3, US4 can be implemented in parallel by different developers (all independent after Foundational)
- US2 and US5 are sequential (US5 needs US2)

---

## Parallel Example: User Story 1

```bash
# Launch all 3 screen adaptations in parallel:
Task: "Update PokemonListScreen.kt in feature/pokemon-list/.../presentation/"
Task: "Update SearchScreen.kt in feature/search/.../presentation/"
Task: "Update FavoritesScreen.kt in feature/favorites/.../presentation/"
```

## Parallel Example: Foundational Phase

```bash
# Launch all foundational components in parallel:
Task: "Create AdaptiveUtils.kt in core/ui/"
Task: "Extract nav items from ModernBottomNav.kt"
Task: "Create AdaptiveNavSuite.kt in core/designsystem/"
Task: "Update PokemonCard.kt in core/designsystem/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T009) — **CRITICAL**
3. Complete Phase 3: User Story 1 (T010-T012)
4. **STOP and VALIDATE**: Test on tablet emulator — grid appears; test on phone — list preserved
5. Demo/deploy if ready — adaptive grid is the most visible improvement

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. Add US1 (Grid/List) → Test independently → **MVP!**
3. Add US3 (Nav Chrome) → Test independently → Enhanced tablet nav
4. Add US2 (Split Pane) → Test independently → Tablet master-detail
5. Add US4 (Detail Layout) → Test independently → Multi-column details
6. Add US5 (Foldable) → Test independently → Foldable support
7. Polish → Edge cases verified → Done

### Sequential Strategy (Single Developer)

Recommended order: Setup → Foundational → US1 → US3 → US2 → US4 → US5 → Polish
(US3 before US2 to avoid `PokedexNavHost.kt` merge conflicts)

### Parallel Team Strategy

With multiple developers:
1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 (PokemonList, Search, Favorites grid)
   - Developer B: US3 (Nav Chrome) + US2 (Split Pane) — sequential due to shared file
   - Developer C: US4 (Detail Layout) + US5 (Foldable) — US5 needs US2, so start with US4
3. Coordinate on `PokedexNavHost.kt` and `PokemonDetailScreen.kt` (changed by multiple stories)

---

## Notes

- [P] tasks = different files, no dependencies — can execute in parallel
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- `PokedexNavHost.kt` is the most conflict-prone file (changed by US3, US2, US5) — coordinate carefully
- `PokemonDetailScreen.kt` is changed by US2 (in-pane variant) and US4 (multi-column) — implement in order
- No new modules — all changes in existing feature/core modules
- All Material 3 adaptive components are alpha (1.5.0-alpha16) — pin versions, no `+`
