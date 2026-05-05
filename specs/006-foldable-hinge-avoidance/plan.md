# Implementation Plan: Foldable Hinge Avoidance

**Branch**: `006-foldable-hinge-avoidance` | **Date**: 2026-05-04 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/006-foldable-hinge-avoidance/spec.md`

## Summary

Implement foldable hinge avoidance for the Pokedex app. When running on a foldable device, the Pokemon list (and other scrollable lists) must dynamically adjust their content padding to prevent items from being drawn over the physical hinge. The solution uses the Jetpack WindowManager library to detect `FoldingFeature` bounds and applies derived `PaddingValues` to Compose `LazyColumn`/`LazyVerticalGrid` via the `contentPadding` parameter.

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose (BOM 2025.03.01), Koin 3.5.6, Retrofit, Room, Coil, Paging 3, Navigation Compose 2.9.0
**New Dependency**: `androidx.window:window:1.3.0` (Jetpack WindowManager for foldable support)
**Storage**: Room (local SQLite database) — unchanged
**Testing**: JUnit, MockK, MockWebServer, Compose Testing API, KoinTest
**Target Platform**: Android (minSdk 26, targetSdk 35)
**Project Type**: android-app (modular)
**Performance Goals**: Cold start < 3s, 60fps UI, smooth scrolling for large lists, real-time hinge layout updates with no perceptible lag
**Constraints**: Offline-first, mid-tier device support, WCAG AA accessibility, Compose-only UI
**Scale/Scope**: Pokedex app with Pokemon list, detail, search, and type browsing features

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

- [x] **Feature-First Modularity**: This is a cross-cutting UI/layout utility. It does not warrant a new `:feature:` module. It will be implemented as a reusable composable/extension in `:core:ui` (or `:core:designsystem`) to be consumed by `:feature:pokemon-list`, `:feature:search`, and any other feature with scrollable lists. No cross-feature dependencies introduced.
- [x] **MVVM + Clean Architecture**: This feature is purely Presentation-layer. It requires no new ViewModels, UseCases, or Repositories. It integrates directly into existing Compose screens.
- [x] **Compose-First UI**: All UI changes are `@Composable` functions. No XML layouts. Existing `LazyColumn`/`LazyVerticalGrid` usage is updated via the `contentPadding` parameter.
- [x] **Test-Guided Development**: Unit tests are not applicable (no business logic). UI tests will verify that `contentPadding` correctly avoids the hinge bounds on foldable emulators. Compose tests will cover the `rememberHingePadding()` utility.
- [x] **Offline-First Data**: Not applicable. This is a pure UI/layout concern with no data persistence or network requirements.
- [x] **Material 3 Design System**: The solution uses standard M3 layout mechanisms (`LazyColumn`, `LazyVerticalGrid`, `PaddingValues`). No custom widgets. Existing M3 tokens remain unchanged.
- [x] **Performance & Memory**: `WindowInfoTracker` Flow collection is bound to the Activity lifecycle using `flowWithLifecycle` (or `repeatOnLifecycle`) to prevent leaks and background processing. No coroutines are launched outside of lifecycle scopes.

## Project Structure

### Documentation (this feature)

```text
specs/006-foldable-hinge-avoidance/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) — SKIPPED (no external contracts)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
app/                                    # Entry point module
├── feature:pokemon-list/               # Pokemon list screen — consume hinge padding
│   ├── src/main/java/.../presentation/ # Update LazyColumn/LazyVerticalGrid contentPadding
├── feature:search/                     # Search feature — consume hinge padding (if applicable)
├── core:ui/                            # Shared UI utilities — ADD: rememberHingePadding()
├── core:data/                          # Shared data (unchanged)
├── core:designsystem/                  # M3 theme, tokens, shared components (unchanged)
├── core:navigation/                    # Navigation graph (unchanged)
└── core:testing/                       # Shared test utilities (add foldable test helpers if needed)

tests/
├── ui/                                 # Compose UI tests for hinge-aware layouts
```

**Structure Decision**: Android modular architecture with feature modules (`:feature:<name>`)
and core shared modules (`:core:<name>`). The hinge avoidance logic is a shared utility in `:core:ui`.

## Complexity Tracking

> No constitution violations identified. This feature is a standard Android foldable pattern and fits cleanly within the existing modular Compose architecture.

## Research Artifacts

- [research.md](research.md): Details on `FoldingFeature`, `WindowInfoTracker`, and Compose `contentPadding` strategy.
- [data-model.md](data-model.md): Confirms no new data entities required.
- [quickstart.md](quickstart.md): Developer guide for consuming `rememberHingePadding()` in feature screens.
