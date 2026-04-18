# Research: Modern Material 3 Design System Update

**Feature**: 002-update-design-system  
**Date**: 2026-04-04  

## Research Tasks

### Task 1: Latest Material 3 Compose Dependencies

**Decision**: Update Compose BOM from `2024.12.01` to `2025.03.01` (latest stable as of April 2026) and update Navigation Compose to `2.9.x`

**Rationale**: The current Compose BOM `2024.12.01` is several months behind. The latest stable BOM includes updated Material 3 components with the latest design tokens, improved PullToRefresh API (non-experimental), and better animation APIs. Navigation Compose 2.9.x includes improved type-safe navigation and transition APIs.

**Alternatives considered**:
- Keep current BOM and only add specific newer libraries → rejected because it creates version conflicts and misses cohesive M3 updates
- Use alpha/beta compose versions → rejected for production stability

**Key dependency updates**:
- `compose-bom`: `2024.12.01` → `2025.03.01` (latest stable)
- `navigation-compose`: `2.8.5` → `2.9.0` (includes `EnterTransition`/`ExitTransition` improvements)
- Add `androidx.compose.material:material-icons-extended` (already available via BOM)
- Add `com.google.android.gms:play-services-base` NOT needed - use bundled fonts instead
- Add `androidx.compose.ui:ui-text-google-fonts` for Google Fonts integration

### Task 2: Google Fonts Integration in Jetpack Compose

**Decision**: Use `createFontFamilyResolver` with bundled TTF/OTF font files in `res/font/` rather than runtime Google Fonts API

**Rationale**: 
- Bundled fonts are available immediately (no network delay)
- Work offline (aligns with offline-first constitution)
- Predictable behavior across devices and Android versions
- No Google Play Services dependency required
- The compose-text-google-fonts library requires runtime fetching which adds complexity

**Implementation approach**:
1. Download Inter (Regular, Medium, SemiBold) and Roboto (Regular, Medium, SemiBold) TTF files
2. Place in `core/designsystem/src/main/res/font/`
3. Create `FontFamily` definitions in `Type.kt`
4. Update `Typography` to use custom font families

**Alternatives considered**:
- `GoogleFont.Provider` API → rejected: requires network, adds latency, doesn't work offline
- System default fonts only → rejected: user specifically requested Google Fonts

### Task 3: M3 Shape Scale Implementation

**Decision**: Create a `Shape.kt` file with M3 shape tokens using `RoundedCornerShape`

**Rationale**: Material 3 defines 5 shape sizes. The current app has NO shape definitions, meaning all components use M3 defaults which are adequate but not customized for the Pokemon theme.

**Shape scale**:
| Token | Size | Usage |
|-------|------|-------|
| `ExtraSmall` | 4dp | Small components, chips, badges |
| `Small` | 8dp | Text fields, search bars |
| `Medium` | 12dp | Cards, list items |
| `Large` | 16dp | Dialogs, navigation drawers |
| `ExtraLarge` | 28dp | Modals, bottom sheets |

**Alternatives considered**:
- Use M3 default shapes without customization → rejected: user wants a distinctive modern look
- Use fully rounded (pill) shapes everywhere → rejected: not M3 compliant, reduces visual hierarchy

### Task 4: Pokemon Type Badge Color Application

**Decision**: Create a shared `TypeBadge` composable in `core:designsystem` that applies the correct `PokemonTypeColors` background with proper text contrast

**Rationale**: The current `TypeBadge` in `PokemonDetailScreen.kt` is a private composable that does NOT apply type-specific colors - it uses `MaterialTheme.colorScheme.onPrimary` for text but no background color. This is a visible bug.

**Implementation**:
- Background: Use the type color directly (e.g., `PokemonTypeColors.Fire`)
- Text color: Calculate luminance of the type color; use white text for dark backgrounds, dark text for light backgrounds
- Shape: `RoundedCornerShape(8.dp)` (Small token)
- Padding: `horizontal = 12dp, vertical = 4dp`
- Style: `labelMedium` typography

**Color contrast strategy**:
```kotlin
fun Color.contrastingTextColor(): Color {
    val luminance = red * 0.299f + green * 0.587f + blue * 0.114f
    return if (luminance > 0.5f) Color.Black else Color.White
}
```

**Alternatives considered**:
- Use tonal container colors (lighter versions) → rejected: less vibrant, harder to distinguish types
- Use outlined badges → rejected: less visually prominent, doesn't match Pokemon aesthetic

### Task 5: Bottom Navigation Bar Integration

**Decision**: Add `NavigationBar` with `NavigationBarItem` to the main screen layout (PokemonList, Search, Favorites) using a shared `PokedexScaffold` wrapper

**Rationale**: Currently navigation is done via TopAppBar icon buttons. A bottom navigation bar is the Material 3 standard for 3-5 primary destinations and provides better thumb reachability.

**Architecture approach**:
- Create a `MainScreenLayout` composable in `app` module that wraps content with `Scaffold` + `NavigationBar`
- The `MainScreenLayout` manages navigation state and displays the correct feature screen
- Search and Favorites become tabs in the bottom nav instead of separate navigation destinations
- PokemonDetail remains a full-screen push navigation (not in bottom nav)

**Navigation items**:
| Icon | Label | Route |
|------|-------|-------|
| `Icons.Default.Pets` (or custom Pokeball) | Pokedex | `PokemonList` |
| `Icons.Default.Search` | Search | `Search` |
| `Icons.Default.Favorite` | Favorites | `Favorites` |

**Alternatives considered**:
- Keep TopAppBar icon navigation → rejected: not modern, poor discoverability
- Use NavigationRail → rejected: better for tablets, not phones
- Use `NavigationBar` in each feature screen → rejected: duplication, inconsistent state

### Task 6: Screen Transition Animations

**Decision**: Use Navigation Compose's `animatedContentScope` with `slideIntoContainer` and `slideOutOfContainer` for navigation transitions

**Rationale**: Navigation Compose 2.8+ supports `enterTransition` and `exitTransition` lambdas on `composable<T>()`. This provides smooth, M3-compliant transitions without additional libraries.

**Transition patterns**:
- List → Detail: Slide in from right, slide out to left
- Detail → List: Slide in from left, slide out to right
- Tab switches (List/Search/Favorites): Fade through or no animation (instant)

**Implementation**:
```kotlin
composable<Route.PokemonDetail>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
    popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) }
) { ... }
```

**Alternatives considered**:
- Accompanist Navigation Animation → rejected: deprecated, merged into Navigation Compose
- Custom `AnimatedContent` transitions → rejected: more complex, Navigation Compose API is sufficient
- Shared element transitions → rejected: not yet stable in Compose, can be added later

### Task 7: PokemonCard Component Design

**Decision**: Create a `PokemonCard` composable in `core:designsystem` that wraps `Card` with M3 styling

**Rationale**: The current `PokemonListItem` uses a basic `Card` with no shape customization, no background tinting, and no elevation. A shared component ensures consistency.

**Design**:
- Shape: `Medium` (12dp rounded corners)
- Elevation: `CardDefaults.cardElevation(defaultElevation = 2.dp)`
- Colors: `CardDefaults.cardColors()` using theme surface variant
- Content padding: 12dp
- Clickable with ripple effect
- Optional favorite indicator overlay

**Alternatives considered**:
- Keep inline Card usage in each feature → rejected: inconsistency, harder to update globally
- Create a full PokemonCard with image+text built-in → rejected: too opinionated, less flexible
