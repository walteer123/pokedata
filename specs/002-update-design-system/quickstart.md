# Quickstart: Design System Update

**Feature**: 002-update-design-system  
**Date**: 2026-04-04  

## Prerequisites

- Android Studio 2024.2+ (Meerkat or later)
- JDK 17
- Android SDK 35
- Emulator or physical device running Android 8.0+ (API 26+)

## Setup

### 1. Clone and Checkout Branch

```bash
git checkout 002-update-design-system
```

### 2. Sync Gradle

After pulling the branch, sync Gradle to download updated dependencies:

```bash
./gradlew --refresh-dependencies
```

### 3. Build the Project

```bash
./gradlew assembleDebug
```

### 4. Run the App

```bash
./gradlew :app:installDebug
```

## What Changed

### Dependencies Updated

| Library | Before | After |
|---------|--------|-------|
| Compose BOM | `2024.12.01` | `2025.03.01` |
| Navigation Compose | `2.8.5` | `2.9.0` |

### New Files Added

```
core/designsystem/src/main/
├── java/com/pokedata/core/designsystem/
│   ├── theme/
│   │   ├── Shape.kt              # NEW: M3 shape tokens
│   │   ├── Color.kt              # UPDATED: Modern M3 color palettes + contrastingTextColor()
│   │   ├── Type.kt               # UPDATED: Google Fonts (Inter + Roboto)
│   │   └── Theme.kt              # UPDATED: Wired up shapes + new colors + animated transitions
│   ├── utils/
│   │   └── StringExtensions.kt   # NEW: capitalizeFirst() utility
│   └── components/
│       ├── TypeBadge.kt          # NEW: Pokemon type badge with correct colors
│       ├── PokemonCard.kt        # NEW: Modern card component
│       └── ModernBottomNav.kt    # NEW: Bottom navigation bar
└── res/font/
    ├── inter_regular.ttf         # NEW: Google Font - Inter
    ├── inter_medium.ttf          # NEW: Google Font - Inter
    ├── inter_semibold.ttf        # NEW: Google Font - Inter
    ├── roboto_regular.ttf        # NEW: Google Font - Roboto
    ├── roboto_medium.ttf         # NEW: Google Font - Roboto
    └── roboto_semibold.ttf       # NEW: Google Font - Roboto
```

### Files Modified

```
app/
└── PokedexNavHost.kt             # Added bottom nav + transitions
feature/pokemon-list/
└── presentation/
    ├── PokemonListScreen.kt      # Integrated PokemonCard, ModernBottomNav
    └── components/PokemonListItem.kt → REPLACED by shared PokemonCard
feature/pokemon-detail/
└── presentation/
    └── PokemonDetailScreen.kt    # Integrated TypeBadge, modern styling
feature/search/
└── presentation/
    └── SearchScreen.kt           # Integrated ModernTopAppBar, ModernBottomNav
feature/favorites/
└── presentation/
    └── FavoritesScreen.kt        # Integrated PokemonCard, ModernBottomNav
```

## Testing the Changes

### Visual Verification

1. **Light Theme**: Open app → verify modern colors, rounded shapes, custom fonts
2. **Dark Theme**: Toggle dark mode → verify adapted dark palette
3. **Type Badges**: Open any Pokemon detail → verify colored type badges
4. **Bottom Nav**: Check all 3 tabs (Pokedex, Search, Favorites)
5. **Transitions**: Navigate list → detail → verify slide animation

### Build Verification

```bash
./gradlew assembleDebug
./gradlew test
```

## Troubleshooting

### Font Files Not Found

Ensure TTF files are in `core/designsystem/src/main/res/font/`. If missing, download from:
- Inter: https://fonts.google.com/specimen/Inter
- Roboto: https://fonts.google.com/specimen/Roboto

### Compose Version Conflicts

If you see version conflict errors:
```bash
./gradlew :app:dependencies --configuration debugRuntimeClasspath
```
Check that all Compose libraries resolve to the same BOM version.

### Navigation Transition Errors

Ensure Navigation Compose is at least `2.8.0` for `slideIntoContainer`/`slideOutOfContainer` APIs.
