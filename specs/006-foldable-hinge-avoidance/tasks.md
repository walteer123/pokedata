# Tasks: Foldable Hinge Avoidance

**Input**: Design documents from `/specs/006-foldable-hinge-avoidance/`
**Prerequisites**: plan.md, spec.md, research.md

**Tests**: UI tests included for foldable hinge behavior validation.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify Jetpack WindowManager dependency is available

- [x] T001 Verify `androidx.window:window` dependency exists in `:core:ui/build.gradle.kts` (already present as `libs.androidx.window.core`)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Create reusable hinge-aware utility in `:core:ui`

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Add `rememberHingeWidth()` composable to `:core:ui/src/main/java/com/pokedata/core/ui/AdaptiveUtils.kt` — returns hinge width in Dp for vertical folds, 0.dp otherwise
- [x] T003 Remove obsolete `rememberHingePadding()` from `AdaptiveUtils.kt` (replaced by `rememberHingeWidth()`)
- [x] T004 Add instrumented UI test for `rememberHingeWidth()` in `:core:ui/src/androidTest/java/com/pokedata/core/ui/FoldingFeatureUtilsTest.kt`

**Checkpoint**: Foundation ready — `rememberHingeWidth()` is available for consumption by feature modules

---

## Phase 3: User Story 1 - Hinge Gap Layout (Priority: P1) 🎯 MVP

**Goal**: When a vertical hinge is present, display content in 2 columns with the hinge gap in the middle. When no hinge, display 3-column grid. Cards are 50% width each (minus gap).

**Independent Test**: On a foldable device with vertical hinge, verify 2 columns with gap in middle. On non-foldable, verify 3-column grid.

### Tests for User Story 1

- [x] T005 [P] [US1] Create smoke test for Pokemon list hinge layout in `:feature:pokemon-list/src/androidTest/java/com/pokedata/feature/pokemonlist/presentation/PokemonListHingeTest.kt`
- [x] T006 [P] [US1] Create smoke test for Search screen hinge layout in `:feature:search/src/androidTest/java/com/pokedata/feature/search/presentation/SearchHingeTest.kt`

### Implementation for User Story 1

- [x] T007 [US1] Refactor `PokemonListContent` in `:feature:pokemon-list/src/main/java/com/pokedata/feature/pokemonlist/presentation/PokemonListScreen.kt`:
  - Replace `useGrid` (WindowWidthSizeClass-based) with `hasVerticalHinge` (hingeWidth > 0.dp)
  - **With hinge**: `LazyColumn` with rows of 2 cards, gap = `hingeWidth`
  - **Without hinge**: `LazyVerticalGrid` with `GridCells.Fixed(3)`, 16dp spacing
  - Remove `windowWidthSizeClass` and `gridState` parameters
- [x] T008 [P] [US1] Refactor `SearchScreen` in `:feature:search/src/main/java/com/pokedata/feature/search/presentation/SearchScreen.kt`:
  - Same logic as T007
  - Remove `windowWidthSizeClass` parameter
- [x] T009 [P] [US1] Update call sites in `:app/src/main/java/com/pokedata/PokedexNavHost.kt` to remove `windowWidthSizeClass` from `PokemonListScreen` and `SearchScreen` calls

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently on foldable devices

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Verify code quality, build health, and documentation accuracy

- [x] T010 [P] Run `./gradlew ktlintCheck` to verify code style compliance
- [x] T011 [P] Run `./gradlew :core:ui:compileDebugKotlin :feature:pokemon-list:compileDebugKotlin :feature:search:compileDebugKotlin :app:compileDebugKotlin` to verify build
- [x] T012 Validate hinge avoidance behavior on foldable emulator (manual testing required)
- [x] T013 Update `quickstart.md` to reflect new `rememberHingeWidth()` API and 2-column layout approach

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **Polish (Phase 4)**: Depends on User Story 1 completion

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) — No dependencies on other stories

### Within Each User Story

- T007 and T008 are parallel since they modify different feature modules
- T009 depends on T007 and T008 (updates call sites)

### Parallel Opportunities

- T005 and T006 (tests) can run in parallel
- T007 and T008 (implementation) can run in parallel
- T010 and T011 (lint/build) can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all implementations for User Story 1 together:
Task: "Refactor PokemonListContent in PokemonListScreen.kt"
Task: "Refactor SearchScreen in SearchScreen.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently on foldable emulator
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently on foldable emulator → Deploy/Demo (MVP!)
3. Each story adds value without breaking previous stories

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
