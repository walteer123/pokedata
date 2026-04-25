# Data Model: Adaptive Layout

**Feature**: 005-adaptive-layout
**Date**: 2026-04-25

## Overview

This feature introduces no new persistent data (no Room entities, no API changes). The data model consists entirely of runtime UI state — screen geometry classification and foldable posture representation. These are observed from platform APIs, not stored.

## Entities

### WindowSizeClass

Represents the device's current screen width category. Drives all layout adaptation decisions.

| Field | Type | Description |
|-------|------|-------------|
| `widthSizeClass` | Enum: `COMPACT`, `MEDIUM`, `EXPANDED` | Screen width category per Material 3 standard |
| `heightSizeClass` | Enum: `COMPACT`, `MEDIUM`, `EXPANDED` | Screen height category (used for landscape posture detection) |

**Thresholds**:
| Class | Width Range | Height Range |
|-------|------------|--------------|
| Compact | < 600dp | < 480dp |
| Medium | 600–839dp | 480–899dp |
| Expanded | >= 840dp | >= 900dp |

**Source**: `currentWindowAdaptiveInfo().windowSizeClass` (Compose, reacts to configuration changes)

**Relationships**: `WindowSizeClass` → determines `LayoutConfiguration` for every screen

**Validation**: Values always present — platform guarantees a valid `WindowSizeClass` at all times

---

### DevicePosture

Represents the physical state of a foldable or dual-screen device.

| Field | Type | Description |
|-------|------|-------------|
| `state` | Enum: `FLAT`, `HALF_OPENED` | Whether the device is fully open or folded at an angle |
| `orientation` | Enum: `VERTICAL`, `HORIZONTAL` | Direction of the fold axis |
| `isSeparating` | Boolean | Whether the fold creates two logical display areas |
| `occlusionType` | Enum: `NONE`, `FULL` | Whether content can be displayed across the fold |
| `bounds` | Rect | Pixel coordinates of the hinge/fold area in window space |

**Posture Mapping**:
| Posture | state | orientation | isSeparating |
|---------|-------|-------------|--------------|
| Fully open (no fold visible) | — | — | — (no FoldingFeature present) |
| Open book (flat, fold visible) | FLAT | VERTICAL | true |
| Tabletop (half-open, horizontal) | HALF_OPENED | HORIZONTAL | — |
| Dual-screen (Surface Duo) | FLAT | VERTICAL | true |

**Source**: `WindowInfoTracker.windowLayoutInfo(activity)` → `FoldingFeature` instances from `androidx.window`

**Relationships**: `DevicePosture` → influences `LayoutConfiguration` (adds hinge constraints)

**Validation**: `FoldingFeature` list may be empty on non-foldable or pre-12L devices (graceful degradation)

---

### LayoutConfiguration

Derived from `WindowSizeClass` and `DevicePosture`. The active combination of layout parameters for the current screen.

| Field | Type | Description |
|-------|------|-------------|
| `windowWidthClass` | WindowWidthSizeClass | Compact, Medium, or Expanded |
| `columnCount` | Int | Number of columns for grid layouts (1 = list mode) |
| `showSplitPane` | Boolean | Whether master-detail split pane is active (>= 840dp) |
| `navStyle` | Enum: `BOTTOM_BAR`, `NAV_RAIL`, `DRAWER` | Which navigation component to display |
| `detailLayout` | Enum: `SINGLE_COLUMN`, `TWO_COLUMN` | Detail screen content arrangement |
| `hingePadding` | Dp? | Padding to apply for hinge avoidance (null if no hinge) |

**Derivation Rules**:
| Condition | columnCount | showSplitPane | navStyle | detailLayout |
|-----------|-------------|---------------|----------|-------------|
| Width < 600dp (COMPACT) | 1 (list) | false | BOTTOM_BAR | SINGLE_COLUMN |
| Width 600–839dp (MEDIUM) | 3-4 (grid) | false | NAV_RAIL | TWO_COLUMN |
| Width >= 840dp (EXPANDED) | 4-5 (grid) | true | DRAWER | TWO_COLUMN (full) / SINGLE_COLUMN (in-pane) |

**Lifecycle**: Computed on every recomposition. Not persisted. Recalculated on configuration change.

**Validation**: `hingePadding` must be > 0 when `DevicePosture` is present with `isSeparating = true` or `state = HALF_OPENED`

---

## State Transition Diagram

```
┌─────────────────────────────────────────────────────┐
│                   Device Rotation                    │
│                    Fold / Unfold                     │
│                 Multi-Window Resize                  │
└──────────────────────┬──────────────────────────────┘
                       │ triggers
                       ▼
┌─────────────────────────────────────────────────────┐
│           currentWindowAdaptiveInfo()                │
│                  WindowInfoTracker                   │
│                                                     │
│    WindowSizeClass  ◄─────────────────────────────►  │
│    DevicePosture (FoldingFeature)                    │
└──────────────────────┬──────────────────────────────┘
                       │ derives
                       ▼
┌─────────────────────────────────────────────────────┐
│              LayoutConfiguration                     │
│                                                     │
│    columnCount  showSplitPane  navStyle              │
│    detailLayout  hingePadding                        │
└──────────────────────┬──────────────────────────────┘
                       │ drives
                       ▼
┌─────────────────────────────────────────────────────┐
│               Compose UI Recomposes                   │
│                                                     │
│    LazyVerticalGrid vs LazyColumn                    │
│    ListDetailPaneScaffold vs Single Pane             │
│    NavigationRail vs NavigationBar                   │
│    Row(detail) vs Column(detail)                     │
│    Modifier.padding(hingePadding)                    │
└─────────────────────────────────────────────────────┘
```

## No Persistent State

- All layout decisions are computed reactively from platform APIs
- No database tables, no SharedPreferences, no saved state bundles for layout parameters
- User-selected Pokemon in split pane is `rememberSaveable` (survives process death but not app restart)
- Nav state (current route) persists via NavHost's internal `SavedStateHandle`
- Scroll position preserved via `rememberLazyListState()` and Compose's `saveable` support
