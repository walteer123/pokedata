# Tasks: Search Bar Animation (Gmail-style)

**Input**: Design documents from `/specs/004-search-bar-animation/`
**Prerequisites**: plan.md (required), spec.md (required for user stories)

**Tests**: Tests are NOT included — the feature specification did not explicitly request TDD.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

---

## Phase 1: Setup (Branch & Environment)

**Purpose**: Initialize the feature branch and verify the existing animation infrastructure is ready.

- [x] T001 Create and checkout feature branch `004-search-bar-animation`
- [x] T002 Verify `SharedTransitionLayout` is active in `app/.../PokedexNavHost.kt` and `sharedBounds` API is available (Compose Animation 1.7.x)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [x] T003 Analyze current `TopAppBar` implementations in `feature/pokemon-list/.../PokemonListScreen.kt` and `feature/search/.../SearchScreen.kt` to plan shared element key matching
- [x] T004 Verify `Route.Search` composable in `app/.../PokedexNavHost.kt` accepts and uses `enterTransition`/`exitTransition` parameters

**Checkpoint**: Foundation ready — shared element API confirmed, navigation transitions configurable, user story implementation can now begin.

---

## Phase 3: User Story 1 — Compact Search Bar in PokemonListScreen (Priority: P1) 🎯 MVP

**Goal**: Replace the search icon in the PokemonListScreen TopAppBar with a compact, rounded search bar that visually matches the Gmail search field. This bar serves as the shared element source.

**Independent Test**: Open the Pokemon list screen and verify the search icon is replaced by a rounded, clickable search hint container ("Search...") in the TopAppBar. Tapping it navigates to the Search screen.

### Implementation for User Story 1

- [x] T005 [P] [US1] Create `SearchBarCompact` composable in `core/designsystem/components/SearchBarCompact.kt` with rounded container, search icon, and hint text
- [x] T006 [US1] Apply `Modifier.sharedBounds` with key `"search-bar"` to `SearchBarCompact` to enable shared element transition
- [x] T007 [US1] Integrate `SearchBarCompact` into `PokemonListScreen.kt` TopAppBar actions, replacing the `IconButton` with `Icons.Default.Search`
- [x] T008 [US1] Ensure `SearchBarCompact` click handler navigates to `Route.Search` using existing `onSearchClick` callback

**Checkpoint**: At this point, User Story 1 should be fully functional — the list screen displays a compact search bar and tapping it navigates to the search screen.

---

## Phase 4: User Story 2 — Expanded Search Bar with Auto-Focus (Priority: P2)

**Goal**: The SearchScreen TopAppBar becomes the shared element target, morphing from the compact bar. The search field receives automatic focus and shows the keyboard when the screen opens.

**Independent Test**: Navigate from the list to the search screen. Verify the search bar visually expands/morphs from the compact position, the text field is focused, and the soft keyboard appears automatically.

### Implementation for User Story 2

- [x] T009 [P] [US2] Apply `Modifier.sharedBounds` with key `"search-bar"` to the `OutlinedTextField` in `SearchScreen.kt` TopAppBar
- [x] T010 [US2] Add `FocusRequester` and `LaunchedEffect(Unit)` in `SearchScreen.kt` to auto-focus the search field and show the keyboard on screen entry
- [x] T011 [US2] Connect `onBackClick` in `PokedexNavHost.kt` to `navController.popBackStack()` for proper reverse animation

**Checkpoint**: At this point, User Stories 1 AND 2 should both work — the compact bar expands into the search field, and going back reverses the animation.

---

## Phase 5: User Story 3 — Smooth Navigation Transitions (Priority: P3)

**Goal**: Add slide + fade transitions to the Search route so the screen content moves smoothly during navigation, complementing the shared element morph on the search bar.

**Independent Test**: Navigate to Search and back. Verify the entire screen slides horizontally with fade, while the search bar morphs independently via shared bounds.

### Implementation for User Story 3

- [x] T012 [US3] Add `enterTransition` (`slideInHorizontally` + `fadeIn`) to `Route.Search` in `PokedexNavHost.kt`
- [x] T013 [US3] Add `exitTransition` (`slideOutHorizontally` + `fadeOut`) to `Route.Search` in `PokedexNavHost.kt`
- [x] T014 [US3] Add `popEnterTransition` and `popExitTransition` to `Route.Search` in `PokedexNavHost.kt` for smooth back navigation
- [x] T015 [US3] Fine-tune animation durations (target 300-400ms) to match Material motion guidelines and feel fluid

**Checkpoint**: All user stories should now be independently functional — the complete Gmail-style search bar animation is implemented.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and validation across all user stories.

- [x] T016 [P] Test navigation with system back button vs. SearchScreen back button to ensure consistent reverse animation
- [x] T017 [P] Verify animation behavior on different screen sizes and orientations
- [x] T018 [P] Ensure `ModernBottomNav` visibility logic in `PokedexNavHost.kt` remains correct during search navigation (no visual glitches)
- [x] T019 Run quickstart validation: tap search bar → type query → navigate back → verify smooth reverse animation
- [x] T020 Update AGENTS.md with new animation pattern details if applicable

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately.
- **Foundational (Phase 2)**: Depends on Setup — BLOCKS all user stories.
- **User Stories (Phase 3–5)**: All depend on Foundational phase. Must execute in priority order (US1 → US2 → US3) because each builds on the previous.
- **Polish (Phase 6)**: Depends on all user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories. Can start after Foundational.
- **User Story 2 (P2)**: Depends on US1 — the shared element source (`SearchBarCompact`) must exist before the target (`SearchScreen` field) can morph.
- **User Story 3 (P3)**: Depends on US1 and US2 — navigation transitions only make sense once the shared element is in place.

### Parallel Opportunities

- T005 and T009 can be started in parallel **after** T003/T004 complete, since they touch different files (`core/designsystem` vs. `feature/search`).
- T010 and T011 can run in parallel (different files: `SearchScreen.kt` vs. `PokedexNavHost.kt`).
- All Phase 6 tasks can run in parallel.

---

## Parallel Example: User Story 1 + User Story 2

```bash
# After Foundational phase completes:
Task: "Create SearchBarCompact in core/designsystem/components/SearchBarCompact.kt"
Task: "Apply sharedBounds to OutlinedTextField in SearchScreen.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1 — compact search bar visible and clickable
4. **STOP and VALIDATE**: Open app, confirm search bar replaces icon, tap navigates to search
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → compact search bar in list screen → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → expanded search bar with morph + auto-focus → Test independently → Deploy/Demo
4. Add User Story 3 → slide/fade transitions → Test independently → Deploy/Demo
5. Add Polish → final refinements

---

## Notes

- No new modules are required — all changes live in existing modules.
- The shared element key `"search-bar"` must match exactly between `SearchBarCompact` and `SearchScreen` `OutlinedTextField`.
- `FocusRequester` requires `androidx.compose.ui.focus` import.
- Animation durations should align with existing detail screen transitions (300ms fade) for visual consistency.
