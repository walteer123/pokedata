# Quickstart: Testing Adaptive Layout

**Feature**: 005-adaptive-layout
**Date**: 2026-04-25

## Prerequisites

- Android Studio Hedgehog (2024.1+) or Ladybug (2025.1+)
- Android Emulator with API 35 system image
- Gradle 8.7+ with JDK 17

## Device Configurations for Testing

### Phone (Compact — < 600dp)

| Emulator | Resolution | dp Width | Tests |
|----------|------------|----------|-------|
| Pixel 8 | 1080x2400 | 411dp | Baseline — all existing behavior unchanged |
| Pixel 8 (landscape) | 2400x1080 | 411dp-height | Verify horizontal list still works, landscape supported |
| Small Phone API 35 | 720x1280 | 320dp | Edge case: very small screen, no content overflow |

**Expected**: Single-column list (`LazyColumn`), bottom navigation bar, single-column detail, push navigation to detail

### Tablet (Medium — 600–839dp)

| Emulator | Resolution | dp Width | Tests |
|----------|------------|----------|-------|
| Pixel Tablet (portrait) | 1600x2560 | 800dp | Grid layout (3-4 columns), navigation rail, multi-column detail on nav push |
| Pixel Tablet (landscape) | 2560x1600 | 1280dp → falls to Expanded | Split pane master-detail, navigation drawer |

**Expected**: Grid layout (`LazyVerticalGrid` with 3+ columns), navigation rail (600–839dp), bottom bar transitions to rail at 600dp

### Large Screen (Expanded — >= 840dp)

| Emulator | Resolution | dp Width | Tests |
|----------|------------|----------|-------|
| Pixel Tablet (landscape) | 2560x1600 | 1280dp | Split pane: list left, detail right |
| Desktop AVD (freeform) | Resizable | Variable | Window resize → layout transitions dynamically |

**Expected**: Two-pane split layout, list tap updates detail inline (no NavHost navigation), navigation drawer

### Foldable

| Emulator | Configuration | Tests |
|----------|--------------|-------|
| Foldable (API 35) | Closed → 400dp | Phone layout (compact) |
| Foldable (API 35) | Open → 800dp | Medium layout (grid, nav rail) |
| Foldable (API 35) | Tabletop posture | Content constrained to top half |
| Foldable (API 35) | Book posture | `ListDetailPaneScaffold` uses `HingePolicy` |

**Expected**: Smooth transition between compact and expanded layouts when folding/unfolding, no content in hinge zone

## Build & Run

```bash
# Ensure all dependencies are resolved
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (connected device/emulator required)
./gradlew connectedAndroidTest

# Build with lint checks
./gradlew lintDebug
```

## Manual Testing Checklist

### P1 — Adaptive Grid/List

- [ ] Phone portrait: Pokemon list in single column
- [ ] Phone landscape: Verify list behavior (may still be single column or very narrow grid — let adaptive decide)
- [ ] Tablet portrait: Pokemon list in multi-column grid (3+ columns)
- [ ] Tablet landscape: More columns than portrait, no empty gaps
- [ ] Tablet rotation: Column count changes without data reload
- [ ] Search results: Same grid behavior on tablet
- [ ] Favorites: Same grid behavior on tablet
- [ ] Pull-to-refresh: Works in both list and grid mode

### P2 — Master-Detail Split Pane

- [ ] Tablet >= 840dp: List on left, detail on right (two-pane)
- [ ] Tap different Pokemon: Detail pane updates immediately, no nav transition
- [ ] Rotate from landscape to portrait (< 840dp): Split pane closes, detail opens full-screen
- [ ] Search in split pane: Results replace list pane, detail pane stays
- [ ] Phone: Tap Pokemon → full-screen detail (push nav, unchanged)
- [ ] Shared element transition: Still works on phone detail push

### P3 — Adaptive Navigation

- [ ] Phone (< 600dp): Bottom navigation bar with 3 tabs
- [ ] Tablet medium (600-839dp): Navigation rail (side) with 3 tabs
- [ ] Tablet expanded (>= 840dp): Navigation drawer (side) with 3 tabs
- [ ] Fold/unfold: Nav component switches without losing current screen
- [ ] Active tab indicator: Correct on all nav styles
- [ ] Tap nav rail item: Content updates, rail highlights correct item

### P4 — Multi-Column Detail

- [ ] Tablet full-screen detail (>= 600dp): Two columns — artwork+info left, stats+abilities right
- [ ] Tablet split-pane detail (narrower): Single-column condensed layout
- [ ] Phone detail: Single column (unchanged)
- [ ] Detail rotation: Layout column count changes with width

### P5 — Foldable

- [ ] Foldable closed: Phone layout (compact)
- [ ] Foldable open: Large screen layout (expanded)
- [ ] Fold/unfold transition: State preserved (scroll, selected item, search text)
- [ ] Tabletop posture: No content in hinge area
- [ ] 300ms transition target: No visible flickering during fold/unfold

### Edge Cases

- [ ] Freeform window resize: Layout thresholds respond dynamically
- [ ] Very small screen (320dp): All content visible, no overflow
- [ ] Split pane + rotate to portrait: Detail preserved, opens full-screen
- [ ] Active search + configuration change: Search text and results preserved
- [ ] Exact threshold (599dp ↔ 600dp): Smooth transition, no flickering

## Known Limitations

- `material3-adaptive-navigation-suite` is alpha (1.5.0-alpha16). API surface may change in future updates. Pin version explicitly.
- Foldable hinge data only available on API 32+ devices. Pre-12L devices display standard layouts without hinge awareness.
- `ListDetailPaneScaffold` does not support shared element transitions on expanded screens (inline detail). This is by design — shared transitions apply only to compact-screen NavHost navigation.
- Freeform multi-window is best-effort. Extreme window sizes (< 320dp or > 2000dp) may not be pixel-perfect.
