# Research: Foldable Hinge Avoidance

## Decision
Use `androidx.window:window-layout` to observe `FoldingFeature` via `WindowInfoTracker` and derive `PaddingValues` for `LazyColumn`/`LazyVerticalGrid` to avoid the hinge area.

## Rationale
- The Jetpack WindowManager library is the canonical Android API for foldable device posture and hinge detection.
- `FoldingFeature` provides precise pixel bounds (`bounds`) and orientation (`HORIZONTAL`/`VERTICAL`), which map directly to `PaddingValues` in Compose.
- Applying padding at the list level (`contentPadding`) is more efficient and less error-prone than offsetting individual items, and it correctly handles scrollbars and overscroll.

## Alternatives Considered
- **Material 3 Adaptive (`ListDetailPaneScaffold`)**: Rejected because the current feature is about a single-pane list, not a list-detail layout. Adaptive scaffolds are overkill for simple hinge avoidance.
- **Manual per-item offset calculation**: Rejected because it introduces unnecessary complexity and potential performance issues during scrolling. `contentPadding` is the Compose-recommended approach.
- **XML `android:layout_margin`**: Rejected because the project is Compose-only.

## Key Findings
- **Dependency**: `androidx.window:window` (which includes `window-layout`).
- **API Entry Point**: `WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity)` returns `Flow<WindowLayoutInfo>`.
- **Relevant Properties**:
  - `FoldingFeature.bounds`: `Rect` in window coordinates.
  - `FoldingFeature.orientation`: `HORIZONTAL` or `VERTICAL`.
  - `FoldingFeature.state`: `FLAT` or `HALF_OPENED`.
  - `FoldingFeature.isSeparating`: Boolean indicating if the feature splits the display.
- **Compose Integration**:
  - Collect the Flow inside a `@Composable` using `produceState` or `LaunchedEffect` + `remember`.
  - Convert `bounds` to `Dp` using `LocalDensity.current`.
  - Map `bounds` and `orientation` to `PaddingValues`.
  - Pass the resulting `PaddingValues` to `contentPadding` in `LazyColumn` or `LazyVerticalGrid`.
- **Lifecycle Safety**: Collection must be bound to the Activity lifecycle (using `flowWithLifecycle` or `repeatOnLifecycle`) to avoid background processing.

## Unknowns Resolved
- How to detect hinge position: `FoldingFeature.bounds`.
- How to apply avoidance in Compose: `contentPadding` with derived `PaddingValues`.
- Where to place the logic: A reusable utility/composable in `:core:ui` or `:core:designsystem`.
