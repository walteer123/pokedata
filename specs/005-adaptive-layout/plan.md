# Implementation Plan: Adaptive Layout (Large Screen, Foldable & Small Screen Support)

**Branch**: `005-adaptive-layout` | **Date**: 2026-04-25 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/005-adaptive-layout/spec.md`

## Summary

Add adaptive/responsive layouts to all 4 screens (PokemonList, PokemonDetail, Search, Favorites) so the app delivers optimal UX on phones, tablets, foldables, and freeform windows. The implementation leverages existing `material3-adaptive-navigation-suite` (1.5.0-alpha16) for `NavigationSuiteScaffold` and adds `ListDetailPaneScaffold` for master-detail on large screens. A new `androidx.window` dependency provides foldable posture detection. No data layer, navigation graph, or ViewModel architecture changes required — this is a pure presentation-layer enhancement using Material 3 window size classes (compact < 600dp, medium 600–839dp, expanded >= 840dp).

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose (BOM 2025.03.01), Material 3 1.4.0, material3-adaptive-navigation-suite 1.5.0-alpha16, Navigation Compose 2.9.0, Koin 3.5.6, Coil 2.7.0, Paging 3
**New Dependencies**: `androidx.window:window-core:1.3.0`, `androidx.window:window-runtime-compose:1.3.0`
**Storage**: Room (unchanged)
**Testing**: JUnit, MockK, Compose Testing API (incl. `createWindowAdaptiveInfo` for size class injection)
**Target Platform**: Android (compileSdk 35, minSdk 26, targetSdk 35)
**Project Type**: android-app (modular)
**Performance Goals**: Layout transitions < 300ms on configuration change, detail pane update < 500ms on item selection, 60fps scrolling in grid layout
**Constraints**: Zero regression on compact screens, offline-first behavior preserved, existing shared element transitions preserved on phones
**Scale/Scope**: 4 screens adapted, 1 new dependency, 0 new modules, ~8 files modified

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Feature-First Modularity**: No new module created. Changes confined to `:app`, `:core:ui`, `:core:designsystem`, and existing `:feature:*` modules. No cross-feature dependencies introduced. Feature modules remain independent.
- [x] **MVVM + Clean Architecture**: ViewModels remain unaware of screen geometry. Window size class is observed via `currentWindowAdaptiveInfo()` exclusively in `@Composable` functions. Presenters (ViewModels) still expose `StateFlow`/`SharedFlow` unchanged.
- [x] **Compose-First UI**: All adaptations use Compose APIs (`LazyVerticalGrid`, `NavigationSuiteScaffold`, `ListDetailPaneScaffold`, `Row`/`Column` conditionals). No XML layouts. Navigation Compose routes unchanged.
- [x] **Test-Guided Development**: Compose UI tests will inject `WindowSizeClass` via `LocalWindowAdaptiveInfo` to verify all layout variants. ViewModel/UseCase tests unaffected. Minimum 70% branch coverage maintained on domain/data layers.
- [x] **Offline-First Data**: Room source of truth, Paging 3, and Retrofit caching untouched. UI layout changes do not affect data flow or caching strategy.
- [x] **Material 3 Design System**: `NavigationSuiteScaffold` and `ListDetailPaneScaffold` are official M3 adaptive components. Thresholds follow M3 window size class conventions. Spacing uses M3 tokens. Dark mode auto-supported.
- [x] **Performance & Memory**: `LazyVerticalGrid` with `contentType` and stable keys replaces `LazyColumn` conditionally. `ListDetailPaneScaffold` manages dual-pane recomposition efficiently. No new coroutine scopes or collectors.

## Project Structure

### Documentation (this feature)

```text
specs/005-adaptive-layout/
├── plan.md              # This file
├── research.md          # Phase 0 output — API analysis, thresholds, patterns
├── data-model.md        # Phase 1 output — adaptive entities
├── quickstart.md        # Phase 1 output — testing guide
├── contracts/           # Phase 1 output — no external interfaces (N/A, skipped)
└── tasks.md             # Phase 2 output (/speckit.tasks — NOT created by /speckit.plan)
```

### Source Code (repository root) — affected files

```text
gradle/
└── libs.versions.toml                   # ADD: window-core, window-runtime-compose (1.3.0)

app/
├── build.gradle.kts                      # ADD: window-core, window-runtime-compose deps
└── src/main/java/com/pokedata/
    ├── MainActivity.kt                   # MODIFY: add enableEdgeToEdge + insets awareness
    └── PokedexNavHost.kt                 # MODIFY: NavigationSuiteScaffold replaces Scaffold;
                                          #          ListDetailPaneScaffold in PokemonList route
core/
├── ui/
│   ├── build.gradle.kts                  # ADD: material3-window-size-class dep (for utility)
│   └── src/main/java/com/pokedata/core/ui/
│       └── AdaptiveUtils.kt              # NEW: rememberWindowSizeClass(), rememberFoldingFeatures()
├── designsystem/
│   ├── build.gradle.kts                  # unchanged (already has material3-adaptive)
│   └── src/main/java/com/pokedata/core/designsystem/components/
│       ├── ModernBottomNav.kt            # MODIFY: extract nav items as data; used by NavigationSuiteScaffold
│       ├── PokemonCard.kt               # MODIFY: flex-height support for grid vs list
│       └── AdaptiveNavSuite.kt          # NEW: NavigationSuiteScaffold wrapper with nav items
feature/
├── pokemon-list/.../presentation/
│   └── PokemonListScreen.kt             # MODIFY: LazyVerticalGrid when size >= Medium
├── pokemon-detail/.../presentation/
│   └── PokemonDetailScreen.kt           # MODIFY: multi-column layout param; standalone variant for split pane
├── search/.../presentation/
│   └── SearchScreen.kt                  # MODIFY: LazyVerticalGrid when size >= Medium
└── favorites/.../presentation/
    └── FavoritesScreen.kt               # MODIFY: LazyVerticalGrid when size >= Medium
```

**Structure Decision**: No new modules. The adaptivity layer is thin: `core:ui` provides reusable window-size-class utilities, `core:designsystem` provides the adaptive nav shell, and each feature screen receives a width class parameter to choose list vs grid. `ListDetailPaneScaffold` lives in `app/PokedexNavHost.kt` as it orchestrates PokemonList + PokemonDetail together.

## Complexity Tracking

No constitution violations. All gates pass.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | — | — |
