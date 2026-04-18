# Data Model: Design System Update

**Feature**: 002-update-design-system  
**Date**: 2026-04-04  

## Overview

This feature does NOT introduce new data entities or modify existing data models. The changes are purely presentational: updating colors, shapes, typography, and UI components. Existing domain models (`PokemonListItem`, `PokemonDetail`) and Room entities remain unchanged.

## Design Token Entities

### Design Tokens (Visual Design Data)

**Color Tokens**:
- Light theme: 12+ M3 color roles (primary, onPrimary, primaryContainer, etc.)
- Dark theme: 12+ M3 color roles with inverted lightness
- Pokemon type colors: 18 type-specific colors with tonal variations

**Shape Tokens**:
- `ExtraSmall` (4dp), `Small` (8dp), `Medium` (12dp), `Large` (16dp), `ExtraLarge` (28dp)
- All use `RoundedCornerShape`

**Typography Tokens**:
- Font families: Inter (body), Roboto (display/headlines)
- Font weights: Regular (400), Medium (500), SemiBold (600)
- 15 M3 text styles (displayLarge through labelSmall)

**Spacing Tokens**:
- M3 standard values: 4dp, 8dp, 12dp, 16dp, 24dp, 32dp

### Shared Component Contracts

**TypeBadge**:
- Input: `typeName: String` (Pokemon type name)
- Output: Colored badge with correct background, readable text, rounded shape
- Behavior: Looks up type color from `PokemonTypeColors`, calculates contrasting text color

**PokemonCard**:
- Input: `modifier`, `onClick`, `content` (slot)
- Output: Styled card with rounded corners, elevation, consistent padding
- Behavior: Applies M3 shape and elevation tokens

**ModernBottomNav**:
- Input: `currentRoute: Any`, `onNavigate: (Route) -> Unit`
- Output: Material 3 navigation bar with 3 items (Pokedex, Search, Favorites)
- Behavior: Highlights active item, triggers navigation on tap

## No Database Changes

- Room schema unchanged
- No migrations required
- No new DAOs or entities

## No API Contract Changes

- PokeAPI integration unchanged
- No new endpoints or DTO modifications
