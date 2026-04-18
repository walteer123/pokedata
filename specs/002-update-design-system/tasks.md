---

description: "Task list for updating design system to modern Material 3"
---

# Tasks: Update Design System to Modern Material 3

**Input**: Design documents from `/specs/002-update-design-system/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/component-contracts.md

**Tests**: Included — Compose UI tests for new shared components (TypeBadge, PokemonCard, ModernBottomNav).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Android modular app**: Feature modules at `feature/<name>/src/main/java/...`, core modules at `core/<name>/src/main/java/...`
- **Presentation layer**: `feature/<name>/.../presentation/` (Composables + ViewModel)
- **Core modules**: `core:designsystem/`, `core:ui/`, `core:navigation/`
- **Tests**: `core/designsystem/src/test/` (unit), `core/designsystem/src/androidTest/` (instrumented)

---

## Phase 1: Setup (Dependency Updates)

**Purpose**: Update version catalog to latest stable Compose and Navigation versions

- [ ] T001 Update `compose-bom` version from `2024.12.01` to `2025.03.01` in `gradle/libs.versions.toml`
- [ ] T002 Update `navigation-compose` version from `2.8.5` to `2.9.0` in `gradle/libs.versions.toml`
- [ ] T003 [P] Run `./gradlew --refresh-dependencies` and verify build succeeds with `./gradlew assembleDebug`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core design system infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 [P] Download and add Inter font files (Regular, Medium, SemiBold) to `core/designsystem/src/main/res/font/inter_regular.ttf`, `inter_medium.ttf`, `inter_semibold.ttf`
- [X] T005 [P] Download and add Roboto font files (Regular, Medium, SemiBold) to `core/designsystem/src/main/res/font/roboto_regular.ttf`, `roboto_medium.ttf`, `roboto_semibold.ttf`
- [X] T006 [P] Create `Shape.kt` with M3 shape tokens (`ExtraSmall`=4dp, `Small`=8dp, `Medium`=12dp, `Large`=16dp, `ExtraLarge`=28dp) in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Shape.kt`
- [X] T007 Update `Color.kt` with modern M3 tonal palettes for light/dark themes, preserving Pokemon-themed primary red and all 18 `PokemonTypeColors` in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Color.kt`
- [X] T008 Update `Type.kt` to define `FontFamily` for Inter and Roboto, apply to all 15 M3 typography styles in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Type.kt`
- [X] T009 Update `Theme.kt` to wire up new `Shapes`, updated `LightColorScheme`/`DarkColorScheme`, and `AppTypography` in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Theme.kt`

**Checkpoint**: Foundation ready — design tokens (colors, shapes, typography) are complete. User story implementation can now begin.

---

## Phase 3: User Story 1 — Modern M3 Visual Design (Priority: P1) 🎯 MVP

**Goal**: All screens display updated Material 3 styling — colors, shapes, typography — with smooth light/dark theme transitions

**Independent Test**: Open the app in both light and dark modes; verify all screens show updated colors, custom fonts, rounded shapes, and modern spacing. Toggle theme and verify smooth transition.

### Implementation for User Story 1

- [X] T010 [US1] Verify `Theme.kt` properly passes `shapes` parameter to `MaterialTheme` composable in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Theme.kt`
- [X] T011 [US1] Verify `MainActivity.kt` correctly wraps content in `PokedexTheme` and `Surface` — no changes needed, confirm in `app/src/main/java/com/pokedata/MainActivity.kt`
- [X] T012 [US1] Update `PokemonListScreen.kt` TopAppBar to use `titleLarge` typography for "Pokedex" title and verify M3 color tokens are applied in `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`
- [X] T013 [US1] Update `PokemonDetailScreen.kt` to use updated typography styles (`titleLarge` for section headers, `bodyMedium` for descriptions) and verify M3 colors in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`
- [X] T014 [US1] Update `SearchScreen.kt` TextField to use M3 `OutlinedTextField` styling with updated typography in `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`
- [X] T015 [US1] Update `FavoritesScreen.kt` to use updated typography and M3 color tokens in `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt`
- [X] T016 [US1] Add `animateEnterExit` or theme transition animation support in `Theme.kt` for smooth light/dark switching in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Theme.kt`

**Checkpoint**: At this point, all 4 screens display modern M3 styling with custom fonts, updated colors, and rounded shapes. Theme transitions are smooth.

---

## Phase 4: User Story 2 — Color-Coded Type Badges (Priority: P1)

**Goal**: Pokemon type badges display with correct type-specific background colors and readable text on the detail screen

**Independent Test**: Navigate to any Pokemon detail screen; verify each type badge shows the correct background color (e.g., red for Fire, blue for Water) with readable contrasting text.

### Tests for User Story 2

- [ ] T017 [P] [US2] Create Compose UI test for `TypeBadge` verifying correct background color for each of 18 Pokemon types in `core/designsystem/src/androidTest/java/com/pokedata/core/designsystem/components/TypeBadgeTest.kt`
- [ ] T018 [P] [US2] Create Compose UI test for `TypeBadge` verifying text contrast ratio meets WCAG AA (4.5:1) in `core/designsystem/src/androidTest/java/com/pokedata/core/designsystem/components/TypeBadgeContrastTest.kt`

### Implementation for User Story 2

- [X] T019 [US2] Create `TypeBadge.kt` shared composable in `core/designsystem` with type color lookup, luminance-based text contrast, `RoundedCornerShape(8.dp)`, and `labelMedium` typography in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/TypeBadge.kt`
- [X] T020 [US2] Add `Color.contrastingTextColor()` extension function for luminance-based text color calculation in `core/designsystem/src/main/java/com/pokedata/core/designsystem/theme/Color.kt` (append to existing file)
- [X] T021 [US2] Replace private `TypeBadge` in `PokemonDetailScreen.kt` with shared `TypeBadge` from designsystem, remove duplicate code in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`

**Checkpoint**: Type badges display correct colors with readable text on all Pokemon detail screens. US1 + US2 together deliver modern visuals + functional type badges.

---

## Phase 5: User Story 3 — Modern Pokemon Cards (Priority: P2)

**Goal**: Pokemon list items display as modern cards with rounded corners, elevation, and consistent M3 spacing

**Independent Test**: Scroll through the Pokemon list; verify each item is a styled card with 12dp rounded corners, 2dp elevation, proper padding, and ripple click feedback.

### Tests for User Story 3

- [ ] T022 [P] [US3] Create Compose UI test for `PokemonCard` verifying card shape, elevation, and layout in `core/designsystem/src/androidTest/java/com/pokedata/core/designsystem/components/PokemonCardTest.kt`

### Implementation for User Story 3

- [X] T023 [US3] Create `PokemonCard.kt` shared composable in `core/designsystem` with M3 `Card` (Medium shape, 2dp elevation), AsyncImage (56dp), name/number text, and optional favorite toggle in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/PokemonCard.kt`
- [X] T024 [US3] Update `PokemonListScreen.kt` to use shared `PokemonCard` instead of local `PokemonListItem` component in `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`
- [ ] T025 [US3] Delete old `PokemonListItem.kt` component from `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/components/PokemonListItem.kt`
- [X] T026 [US3] Update `SearchScreen.kt` search results to use shared `PokemonCard` in `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`
- [X] T027 [US3] Update `FavoritesScreen.kt` favorites list to use shared `PokemonCard` in `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt`

**Checkpoint**: All Pokemon list items across List, Search, and Favorites screens use consistent modern card styling. US1 + US2 + US3 deliver complete visual modernization.

---

## Phase 6: User Story 4 — Bottom Navigation Bar (Priority: P2)

**Goal**: Primary navigation uses Material 3 bottom navigation bar with animated selection indicators for Pokedex, Search, and Favorites

**Independent Test**: View bottom nav bar on all primary screens; tap each item and verify animated selection indicator updates and correct screen displays.

### Tests for User Story 4

- [ ] T028 [P] [US4] Create Compose UI test for `ModernBottomNav` verifying 3 nav items, active state highlighting, and click callbacks in `core/designsystem/src/androidTest/java/com/pokedata/core/designsystem/components/ModernBottomNavTest.kt`

### Implementation for User Story 4

- [X] T029 [US4] Create `ModernBottomNav.kt` shared composable with M3 `NavigationBar` + 3 `NavigationBarItem` (Pokedex, Search, Favorites) with icon/label/active state in `core/designsystem/src/main/java/com/pokedata/core/designsystem/components/ModernBottomNav.kt`
- [X] T030 [US4] Refactor `PokedexNavHost.kt` to use a `Scaffold` with `bottomBar` containing `ModernBottomNav`, manage navigation state with `currentBackStackEntryAsState()`, and display feature screens as content in `app/src/main/java/com/pokedata/PokedexNavHost.kt`
- [X] T031 [US4] Update `PokemonListScreen.kt` to remove TopAppBar Search/Favorite icon buttons (navigation now via bottom bar) in `feature/pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`
- [X] T032 [US4] Update `SearchScreen.kt` to remove back button navigation (now a bottom nav tab) and integrate with bottom nav state in `feature/search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`
- [X] T033 [US4] Update `FavoritesScreen.kt` to remove back button navigation (now a bottom nav tab) and integrate with bottom nav state in `feature/favorites/src/main/java/com/pokedata/feature/favorites/presentation/FavoritesScreen.kt`
- [X] T034 [US4] Ensure `PokemonDetailScreen.kt` remains full-screen push navigation (NOT in bottom nav) with back arrow — no bottom bar on detail screen in `feature/pokemon-detail/src/main/java/com/pokedata/feature/pokemondetail/presentation/PokemonDetailScreen.kt`

**Checkpoint**: Bottom navigation bar is functional across all primary screens. PokemonDetail remains a push navigation. All 4 user stories now deliver a complete modern UX.

---

## Phase 7: User Story 5 — Smooth Screen Transitions (Priority: P3)

**Goal**: Navigation between screens uses smooth Material 3 animations — slide transitions for detail screen, fade-through for tab switches

**Independent Test**: Tap a Pokemon in the list → verify slide-in animation; press back → verify slide-out animation; switch between bottom nav tabs → verify smooth transition.

### Implementation for User Story 5

- [X] T035 [US5] Add `enterTransition`, `exitTransition`, `popEnterTransition`, `popExitTransition` to `Route.PokemonDetail` composable in `PokedexNavHost.kt` using `slideIntoContainer`/`slideOutOfContainer` (SlideDirection.Start/End) in `app/src/main/java/com/pokedata/PokedexNavHost.kt`
- [X] T036 [US5] Add fade-through or no-animation transitions for bottom nav tab switches (List ↔ Search ↔ Favorites) in `app/src/main/java/com/pokedata/PokedexNavHost.kt`
- [X] T037 [US5] Import required transition APIs (`AnimatedContentTransitionScope`, `slideIntoContainer`, `slideOutOfContainer`, `fadeIn`, `fadeOut`) in `app/src/main/java/com/pokedata/PokedexNavHost.kt`

**Checkpoint**: All screen transitions are smooth and M3-compliant. All 5 user stories are complete.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Quality assurance, accessibility verification, and final cleanup

- [ ] T038 [P] Verify WCAG AA contrast ratios (4.5:1 for normal text, 3:1 for large text) on all screens — document results in `specs/002-update-design-system/accessibility-report.md`
- [ ] T039 [P] Test light/dark theme transitions on all 4 screens — verify no visual glitches or inconsistent states
- [ ] T040 [P] Verify screen transitions run at 60fps with no dropped frames using Android Studio Profiler
- [X] T041 Run `./gradlew assembleDebug` and verify full build succeeds with no compilation errors
- [X] T042 Run `./gradlew :core:designsystem:test` and verify all component tests pass
- [X] T043 Run `./gradlew ktlintCheck` and fix any formatting violations
- [X] T044 Update `quickstart.md` with any final setup instructions or troubleshooting notes in `specs/002-update-design-system/quickstart.md`
- [ ] T045 Visual QA: manually verify all 5 user stories on an emulator or physical device

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3–7)**: All depend on Foundational phase completion
  - User stories can proceed sequentially in priority order (P1 → P2 → P3)
  - US1 and US2 can be combined (both P1, share foundational work)
  - US3 and US4 can be combined (both P2, independent concerns)
  - US5 depends on US4 (transitions require bottom nav structure)
- **Polish (Phase 8)**: Depends on all user stories being complete

### User Story Dependencies

```
Phase 1 (Setup) → Phase 2 (Foundational)
                        ↓
                   US1 (P1) ──────────────────────────┐
                        ↓                              │
                   US2 (P1) ───────────────────────────┤
                        ↓                              │ All depend on
                   US3 (P2) ───────────────────────────┤ Foundational
                        ↓                              │
                   US4 (P2) ───────────────────────────┤
                        ↓                              │
                   US5 (P3) ───────────────────────────┘
                        ↓
                  Phase 8 (Polish)
```

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Shared components before screen integration
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- **Phase 1**: T004, T005, T006 can run in parallel (font files + shapes are independent)
- **Phase 2**: T007 (Color), T008 (Type) can run in parallel; T009 (Theme) depends on both
- **Phase 3**: T012, T013, T014, T015 can run in parallel (each screen is independent)
- **Phase 4**: T017, T018 can run in parallel (independent tests); T019, T020 can run in parallel
- **Phase 5**: T022 (test) can run in parallel with T023 (implementation); T024, T026, T027 can run in parallel (3 screens)
- **Phase 6**: T028 (test) can run in parallel with T029 (implementation); T031, T032, T033 can run in parallel (3 screens)
- **Phase 8**: T038, T039, T040 can run in parallel (independent verification tasks)

---

## Parallel Example: User Story 1 (Modern M3 Visual Design)

```bash
# After Foundational phase is complete, update all 4 screens in parallel:
Task: "Update PokemonListScreen TopAppBar typography and colors" (T012)
Task: "Update PokemonDetailScreen typography and colors" (T013)
Task: "Update SearchScreen TextField styling" (T014)
Task: "Update FavoritesScreen typography and colors" (T015)
```

## Parallel Example: User Story 3 (Modern Pokemon Cards)

```bash
# After PokemonCard component is created, integrate into all 3 screens in parallel:
Task: "Update PokemonListScreen to use PokemonCard" (T024)
Task: "Update SearchScreen to use PokemonCard" (T026)
Task: "Update FavoritesScreen to use PokemonCard" (T027)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (3 tasks)
2. Complete Phase 2: Foundational (6 tasks) — CRITICAL, blocks all stories
3. Complete Phase 3: User Story 1 (7 tasks)
4. **STOP and VALIDATE**: Open app, verify modern M3 colors, fonts, shapes on all screens
5. Deploy/demo if ready — app looks modern even without bottom nav or type badges

### Incremental Delivery

1. Setup + Foundational → Design system foundation ready
2. Add US1 → All screens have modern M3 styling → Test → Demo (MVP!)
3. Add US2 → Type badges work with correct colors → Test → Demo
4. Add US3 → Pokemon cards are modern → Test → Demo
5. Add US4 → Bottom navigation bar → Test → Demo
6. Add US5 → Smooth transitions → Test → Demo
7. Each increment adds visible value without breaking previous work

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 + US2 (visual design + type badges)
   - Developer B: US3 + US4 (cards + bottom nav)
   - Developer C: US5 (transitions, after US4 structure exists)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [US1–US5] label maps task to specific user story for traceability
- Each user story is independently completable and testable
- Font files (TTF) must be downloaded separately and placed in `res/font/` — not auto-generated
- `PokemonDetailScreen` does NOT show bottom navigation bar (push navigation only)
- Bottom nav replaces TopAppBar icon buttons for Search/Favorites on the list screen
- All existing business logic, ViewModels, and repositories remain unchanged
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
