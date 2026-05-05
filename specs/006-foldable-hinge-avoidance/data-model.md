# Data Model: Foldable Hinge Avoidance

## Overview
This feature is a **UI/Layout enhancement** and does not introduce any new domain entities, database tables, or network data models.

## Entities
No new entities are required.

## Existing Entities Affected
- **Pokemon list UI (`:feature:pokemon-list`)**: Consumes `PaddingValues` to adjust `LazyColumn`/`LazyVerticalGrid` `contentPadding`.
- **Search UI (`:feature:search`)**: May reuse the same padding utility if it displays a scrollable list.

## State
- **FoldingFeature State**: Observed via `Flow<WindowLayoutInfo>` from `WindowInfoTracker`. Converted to a `PaddingValues` state object in Compose.
- No persistent state (Room) or network state is involved.
