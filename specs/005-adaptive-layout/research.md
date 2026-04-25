# Research: Adaptive Layout (Large Screen, Foldable & Small Screen Support)

**Feature**: 005-adaptive-layout
**Date**: 2026-04-25

## 1. Window Size Class Detection

### Decision
Use `currentWindowAdaptiveInfo()` from `material3-window-size-class` (included in core M3 1.4.0) to detect window width categories. No custom size detection logic.

### Rationale
- Already in the project's version catalog (`material3-window-size-class` at 1.4.0)
- Standard Material 3 API — compatible with all Material 3 components
- Provides `WindowWidthSizeClass.COMPACT`, `.MEDIUM`, `.EXPANDED`
- Reacts to runtime changes (rotation, fold/unfold, multi-window resize) automatically via `currentWindowAdaptiveInfo()`

### Alternatives Considered
- **`BoxWithConstraints` + manual threshold check**: Works but duplicates M3 conventions, doesn't integrate with adaptive scaffold components, requires polling for configuration changes
- **`LocalConfiguration.current.screenWidthDp`**: Lower-level, no classification convenience, doesn't react to windowing (multi-window) changes reliably
- **Custom ViewModel exposing size class**: Antipattern — screen geometry is pure UI concern, should not leak into ViewModel

### Thresholds (Material 3 Standard)
| Class | Width Range | Typical Devices |
|-------|------------|-----------------|
| Compact | < 600dp | Phones (portrait), foldables closed |
| Medium | 600–839dp | Tablets (portrait), foldables open (narrow), phones (landscape) |
| Expanded | >= 840dp | Tablets (landscape), foldables open (wide), desktops |

## 2. Adaptive Navigation Shell

### Decision
Replace `Scaffold` + `ModernBottomNav` with `NavigationSuiteScaffold` from `material3-adaptive-navigation-suite` 1.5.0-alpha16.

### Rationale
- Library already in project classpath (both `app` and `core:designsystem` depend on it)
- Auto-switches between `NavigationBar` (compact), `NavigationRail` (medium), and `PermanentNavigationDrawer` (expanded)
- Single component replaces manual scaffold + size-check + conditional nav rendering
- `NavigationSuiteScope` DSL provides structured way to declare nav items
- Integrates with Navigation Compose via `navController` binding

### Alternatives Considered
- **Manual `if/else` on window size + separate Scaffolds**: More code, harder to maintain, duplicates nav item logic
- **Keeping ModernBottomNav + adding manual NavigationRail**: Fragments the nav logic, harder to ensure consistent transition behavior
- **`androidx.compose.material3.adaptive.adaptiveLayoutDirective`**: Can be used but less structured than `NavigationSuiteScaffold`

### Implementation Pattern
```kotlin
NavigationSuiteScaffold(
    navigationSuiteItems = {
        navItems.forEach { item ->
            item(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) { ... } }
            )
        }
    }
) { paddingValues ->
    // NavHost content with paddingValues applied
}
```

## 3. Master-Detail Split Pane

### Decision
Use `ListDetailPaneScaffold` inside the PokemonList route composable to show list + detail side-by-side on Expanded (>=840dp) screens. On Compact/Medium, fall back to single-pane with NavHost-based detail navigation.

### Rationale
- `ListDetailPaneScaffold` handles ALL state management: pane expansion/collapse, back navigation, selection tracking
- Built-in `HingePolicy` handles foldable hinge avoidance automatically
- On Expanded: both panes visible, tapping list item updates detail inline (no NavHost navigation)
- On Compact: single pane, tapping item triggers NavHost navigation (preserving existing shared element transitions)
- Detail pane reuses the same `PokemonDetailScreen` composable — just a different context (inline vs full-screen)

### Alternatives Considered
- **Manual `Row` with two panes and custom selection state**: Much more code, need to handle back navigation, pane resizing, configuration changes manually
- **`SupportingPaneScaffold`**: Designed for supplementary content, not for master-detail navigation pattern
- **Two separate NavHosts**: Overengineered; `ListDetailPaneScaffold` handles all necessary orchestration

### Pane Architecture
```
composable<Route.PokemonList> {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val selectedPokemon = rememberSaveable { mutableStateOf<Int?>(null) }

    ListDetailPaneScaffold(
        directive = adaptiveInfo.adaptiveLayoutDirective,
        listPane = {
            PokemonListScreen(
                onPokemonClick = { id ->
                    if (isExpanded) selectedPokemon.value = id
                    else navController.navigate(Route.PokemonDetail(id, ...))
                },
                windowSizeClass = adaptiveInfo.windowSizeClass.windowWidthSizeClass
            )
        },
        detailPane = {
            selectedPokemon.value?.let { id ->
                PokemonDetailScreen(
                    pokemonId = id,
                    windowSizeClass = adaptiveInfo.windowSizeClass.windowWidthSizeClass,
                    isInPane = true
                )
            } ?: PlaceholderEmptyState()
        }
    )
}
```

### Shared Transition Compatibility
- On Expanded (both panes): No navigation event occurs → shared element transitions do not fire. This is correct — the user sees both panes, no transition needed.
- On Compact (single pane): NavHost navigation to `Route.PokemonDetail` fires normally → existing `SharedTransitionLayout` + `sharedBounds()` work as before.
- Zero regression: existing transition quality preserved exactly.

## 4. Adaptive List vs Grid Layout

### Decision
Pass `WindowWidthSizeClass` parameter to collection screens. When >= MEDIUM: use `LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 160.dp))`. When COMPACT: use existing `LazyColumn`.

### Rationale
- `GridCells.Adaptive(160.dp)` auto-calculates column count based on available width — no manual column math needed
- `160dp` minimum card width gives 4-5 columns on tablets (landscape), 3 on tablets (portrait), 1 on small phones
- Same `PokemonCard` composable works in both list and grid — only the container changes
- Paging 3 `LazyPagingItems` works identically with `LazyColumn` and `LazyVerticalGrid`

### Alternatives Considered
- **`GridCells.Fixed(n)` with manual column calculation**: Overly rigid, doesn't adapt well to freeform window resize
- **`FlowRow` arrangement**: Not lazy, can't paginate efficiently
- **Separate grid card variant**: Unnecessary duplication — `PokemonCard` with `fillMaxWidth` works in both contexts

### Column Density (reference)
| Width | Columns (Adaptive 160.dp) |
|-------|--------------------------|
| 320dp (phone) | 2 (but overridden to 1 via LazyColumn) |
| 600dp (tablet portrait) | 3 |
| 800dp (tablet landscape) | 5 |
| 1024dp (desktop) | 6 |

## 5. Multi-Column Detail Layout

### Decision
Add `isWideLayout: Boolean` parameter to `PokemonDetailScreen`. When true: `Row { Column(artwork+info), Column(stats+abilities) }`. When false: existing single `Column` (unchanged).

### Rationale
- Simple boolean toggle — no complex layout engine needed
- Wide = full-width context (Expanded full screen or Medium standalone detail)
- Narrow = in-pane context or Compact screen
- All existing composable pieces (artwork, types, stats rows) remain reusable within the columns
- `verticalScroll` modifier applied to each column independently so they scroll in sync

### Alternatives Considered
- **`FlowRow` with rearranged sections**: Less predictable, harder to maintain visual hierarchy
- **Column-based layout with `BoxWithConstraints` internal detection**: Couples layout detection to content, harder to test
- **Separate `PokemonDetailWideScreen` composable**: Unnecessary duplication; single composable with conditional branching is cleaner

## 6. Foldable & Hinge Awareness

### Decision
Add `androidx.window:window-core:1.3.0` + `androidx.window:window-runtime-compose:1.3.0` to detect `FoldingFeature` instances. Use `ListDetailPaneScaffold`'s built-in `HingePolicy` for book-style folds. Apply manual `Modifier.padding()` for tabletop posture content avoidance.

### Rationale
- `androidx.window` is the official Jetpack library for foldable/dual-screen detection
- `WindowInfoTracker` provides reactive `Flow<WindowLayoutInfo>` with `FoldingFeature` data
- Backported to API 21+ (safe on minSdk 26 — returns empty feature list on pre-12L devices)
- `ListDetailPaneScaffold` handles book-posture hinge avoidance automatically via `HingePolicy`
- Tabletop posture (HALF_OPENED + HORIZONTAL) requires manual intervention — content constrained to top half

### Alternatives Considered
- **Platform-only `Display.getDisplay()` API**: Not available on older devices, no reactive observation
- **Ignoring foldable specifics**: Violates spec requirement FR-010; content would be obscured on foldable devices
- **Custom `WindowManager` integration**: Rebuilds what `androidx.window` already provides

### Dependency
```toml
# libs.versions.toml
[versions]
window = "1.3.0"

[libraries]
androidx-window-core = { group = "androidx.window", name = "window-core", version.ref = "window" }
androidx-window-compose = { group = "androidx.window", name = "window-runtime-compose", version.ref = "window" }
```

```kotlin
// app/build.gradle.kts
implementation(libs.androidx.window.core)
implementation(libs.androidx.window.compose)
```

### FoldingFeature Key Fields
| Field | Description |
|-------|-------------|
| `state` | `FLAT` (fully open) or `HALF_OPENED` (angled) |
| `orientation` | `VERTICAL` (book fold) or `HORIZONTAL` (tabletop) |
| `occlusionType` | `NONE` (can display across) or `FULL` (avoid entirely) |
| `isSeparating` | True if fold creates two logical display areas |
| `bounds` | `Rect` in window coordinates of the hinge area |

### Posture Handling
| Posture | Detection | Layout Action |
|---------|-----------|--------------|
| Closed (compact) | WindowSizeClass COMPACT | Phone layouts (automatic) |
| Open book (flat, non-separating) | WindowSizeClass EXPANDED, no fold | Large screen layouts (automatic) |
| Open book (flat, separating) | EXPANDED + FoldingFeature separating | `ListDetailPaneScaffold` HingePolicy auto-pans |
| Tabletop (half-open, horizontal) | HALF_OPENED + HORIZONTAL | Constrain content to top half, bottom half shows simplified controls or is empty |
| Full screen (expanded, any) | WindowSizeClass EXPANDED | Split pane + multi-column (automatic) |

## 7. Module Responsibility

| Module | New/Changed Responsibility |
|--------|---------------------------|
| `gradle/libs.versions.toml` | Add `window` version and library entries |
| `app` | Add window dependencies; `NavigationSuiteScaffold` + `ListDetailPaneScaffold` wiring |
| `core:ui` | Add `AdaptiveUtils.kt` — `rememberWindowSizeClass()`, `rememberFoldingFeatures()` |
| `core:designsystem` | Add `AdaptiveNavSuite.kt` — wrap `NavigationSuiteScaffold`; update `PokemonCard.kt` for grid; extract nav item data from `ModernBottomNav.kt` |
| `feature:pokemon-list` | Accept `WindowWidthSizeClass` param; conditionally use `LazyVerticalGrid` |
| `feature:pokemon-detail` | Accept `isWide` param; conditional multi-column `Row` |
| `feature:search` | Accept `WindowWidthSizeClass` param; conditionally use `LazyVerticalGrid` |
| `feature:favorites` | Accept `WindowWidthSizeClass` param; conditionally use `LazyVerticalGrid` |

## 8. Testing Strategy

| Test Type | Approach |
|-----------|----------|
| **UI — list vs grid** | `createAndroidComposeRule` + `ComposeContentTestRule` with `LocalWindowAdaptiveInfo` overridden to Compact/Medium/Expanded. Assert `LazyColumn` vs `LazyVerticalGrid` node presence. |
| **UI — split pane** | Test on Expanded: assert two panes visible, tap list item → detail pane updates. Test on Compact: assert single pane, tap item → NavHost navigation occurs. |
| **UI — nav chrome** | Assert `NavigationBar` sibling on Compact, `NavigationRail` sibling on Medium/Expanded. |
| **UI — detail multi-column** | Assert `Row` layout on wide, `Column` layout on compact. |
| **UI — foldable** | Test with mocked `WindowLayoutInfo` containing `FoldingFeature(HALF_OPENED, HORIZONTAL)`. Assert padding applied for hinge. |
| **Unit** | ViewModel tests unchanged. `AdaptiveUtils.kt` pure functions tested for correct size class mapping. |
| **Integration** | Existing Paging 3 + Room integration tests unchanged. |

## 9. Risk Assessment

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| `material3-adaptive-navigation-suite` API changes (alpha) | Medium | Pin exact version 1.5.0-alpha16; no `+` in version ref |
| `ListDetailPaneScaffold` back stack conflicts with NavHost | Low | Only used in single route; compact fallback preserves NavHost behavior |
| Grid layout performance on low-end tablets | Low | `LazyVerticalGrid` with `contentType` + stable keys; bounded Coil cache |
| Foldable detection on pre-12L devices | None | `androidx.window` returns empty features gracefully |
| Shared element transition regression | None | Compact path unchanged; expanded path intentionally skips transitions |
