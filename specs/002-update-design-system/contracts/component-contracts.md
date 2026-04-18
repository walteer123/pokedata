# Component Contracts: Design System Shared Components

**Feature**: 002-update-design-system  
**Date**: 2026-04-04  

## TypeBadge

### Purpose
Display a Pokemon type as a color-coded badge with proper background color and readable text.

### Signature
```kotlin
@Composable
fun TypeBadge(
    typeName: String,
    modifier: Modifier = Modifier
)
```

### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `typeName` | `String` | Yes | Pokemon type name (e.g., "fire", "water", "grass") — case-insensitive |
| `modifier` | `Modifier` | No | Compose modifier for external styling |

### Behavior
1. Look up background color from `PokemonTypeColors.getColor(typeName)`
2. Calculate contrasting text color based on background luminance
3. Display type name (capitalized) with `labelMedium` typography
4. Apply `RoundedCornerShape(8.dp)` shape
5. Apply internal padding: `horizontal = 12dp, vertical = 4dp`

### Visual Specs
- Minimum height: 24dp
- Text: `labelMedium`, single line, uppercase or capitalized
- Background: Solid type color (no gradient)
- Text color: White on dark backgrounds, dark gray on light backgrounds
- Shape: 8dp rounded corners

### Accessibility
- Text contrast ratio >= 4.5:1 against background
- Content description: "{typeName} type"

---

## PokemonCard

### Purpose
Display a Pokemon item in a list with modern card styling, used across list, search, and favorites screens.

### Signature
```kotlin
@Composable
fun PokemonCard(
    id: Int,
    name: String,
    number: Int,
    spriteUrl: String?,
    isFavorite: Boolean,
    onClick: (Int) -> Unit,
    onFavoriteToggle: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
)
```

### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | `Int` | Yes | Pokemon ID |
| `name` | `String` | Yes | Pokemon name (will be capitalized internally) |
| `number` | `Int` | Yes | Pokedex number (displayed as "#N") |
| `spriteUrl` | `String?` | Yes | URL for Pokemon sprite image |
| `isFavorite` | `Boolean` | Yes | Whether the Pokemon is favorited |
| `onClick` | `(Int) -> Unit` | Yes | Callback when card is tapped |
| `onFavoriteToggle` | `((Int) -> Unit)?` | No | Callback for favorite toggle; if null, heart icon is hidden |
| `modifier` | `Modifier` | No | Compose modifier for external styling |

### Behavior
1. Render as M3 `Card` with `Medium` shape (12dp corners)
2. Card elevation: `2dp` default
3. Content layout: Row with AsyncImage (56dp) + Column (name + number) + optional Favorite IconButton
4. Image: 56dp, `Crop` content scale, with placeholder and error handling
5. Name: `titleMedium` typography, single line, ellipsis overflow
6. Number: `bodySmall` typography, `onSurfaceVariant` color
7. Favorite toggle: 48x48dp IconButton, filled/outline heart icon
8. Clickable with ripple effect (M3 default)

### Visual Specs
- Card shape: `RoundedCornerShape(12.dp)`
- Card elevation: `2dp`
- Internal padding: `12dp`
- Image size: `56dp`
- Image-to-text spacing: `12dp`
- Card spacing (external): `horizontal = 16dp, vertical = 4dp`

---

## ModernBottomNav

### Purpose
Material 3 bottom navigation bar for primary app navigation between Pokedex, Search, and Favorites.

### Signature
```kotlin
@Composable
fun ModernBottomNav(
    currentRoute: Any,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier
)
```

### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `currentRoute` | `Any` | Yes | Current active route (for highlighting) |
| `onNavigate` | `(Route) -> Unit` | Yes | Callback when a nav item is tapped |
| `modifier` | `Modifier` | No | Compose modifier for external styling |

### Navigation Items
| Route | Icon | Label |
|-------|------|-------|
| `Route.PokemonList` | `Icons.Default.Pets` (or custom Pokeball) | "Pokedex" |
| `Route.Search` | `Icons.Default.Search` | "Search" |
| `Route.Favorites` | `Icons.Default.Favorite` | "Favorites" |

### Behavior
1. Display 3 navigation items with icon + label
2. Highlight active item based on `currentRoute` type matching
3. On tap: call `onNavigate(route)` with the corresponding route
4. Use M3 `NavigationBar` + `NavigationBarItem` components
5. Active item: filled icon + primary color indicator
6. Inactive item: outlined icon + onSurfaceVariant color

### Visual Specs
- Component: `NavigationBar`
- Item height: 80dp (M3 default)
- Icon size: 24dp
- Label: `labelMedium` typography
- Active indicator: M3 default pill shape

---

## ModernTopAppBar (Optional Wrapper)

### Purpose
Provide a consistent M3 TopAppBar style across all screens with optional search integration.

### Signature
```kotlin
@Composable
fun ModernTopAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
)
```

### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `title` | `String` | Yes | App bar title text |
| `navigationIcon` | `@Composable (() -> Unit)?` | No | Left-side navigation icon (back arrow, etc.) |
| `actions` | `@Composable RowScope.() -> Unit` | No | Right-side action buttons |
| `modifier` | `Modifier` | No | Compose modifier for external styling |

### Behavior
1. Wrap M3 `TopAppBar` with consistent styling
2. Title uses `titleLarge` typography
3. Navigation icon slot (optional)
4. Actions slot (optional)
5. Scrolled behavior: optional elevation on scroll (via `scrollBehavior`)
