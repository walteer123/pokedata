# Research: Shared Element Transitions for Pokemon Detail

**Date**: 2026-04-18
**Feature**: 003-detail-transition

## Decision 1: Shared Element API

- **Decision**: Use Compose Shared Element Transitions API (SharedTransitionLayout + sharedBounds/sharedElement with AnimatedContentScope)
- **Rationale**: Compose BOM 2025.03.01 ships Compose Animation 1.7.x and Compose UI 1.7.x+, which include the stable Shared Element Transitions API. Navigation Compose 2.9.0 fully supports passing AnimatedContentScope into composable lambdas. This is the official, recommended approach for shared element transitions in Jetpack Compose.
- **Alternatives considered**:
  - Custom animation with animateBoundsAsState — More manual control but harder to synchronize with navigation transitions. Rejected because the official API is stable and purpose-built for this use case.
  - SharedElementCallback via AndroidX Transitions (View system) — Not applicable; this is a View-system API and the project is 100% Compose.

## Decision 2: sharedElement vs sharedBounds

- **Decision**: Use sharedBounds for the Pokemon image, not sharedElement.
- **Rationale**: The list card uses spriteUrl (small pixel art) and the detail screen uses artworkUrl (high-res official artwork). These are different images at different URLs. sharedElement requires identical content to produce a pixel-perfect morph — it would fail or produce jarring results with different images. sharedBounds animates the visual bounds (size, position) of the element while allowing the content to change, which is the correct approach when the image source differs.
- **Alternatives considered**:
  - sharedElement — Only works when the composable content is identical. Since spriteUrl and artworkUrl differ, the crossfade would break the morph effect.
  - Only slide transition (current) — This is what we have today; the user explicitly wants a richer experience.

## Decision 3: Route parameter enrichment

- **Decision**: Add spriteUrl and name as parameters to Route.PokemonDetail.
- **Rationale**: The detail screen currently fetches data from the ViewModel, which loads via network. Without the sprite URL in the route, the detail screen has no image to show during the shared element transition (the ViewModel data hasn't loaded yet). Passing spriteUrl (and name for the title) as route parameters allows the detail screen to render immediately with the correct placeholder/sprite, giving a seamless shared element experience. The ViewModel then loads the full detail data in the background.
- **Alternatives considered**:
  - Don't pass spriteUrl — The detail screen would show a blank/placeholder image during the transition, breaking the visual continuity. The user would see the image jump into place then suddenly change when data loads.
  - Pass only spriteUrl, derive name from ViewModel — Possible but inconsistent; the title would pop in after a delay.

## Decision 4: PokemonCard API change

- **Decision**: Add an optional imageModifier parameter to PokemonCard rather than making it accept SharedTransitionScope.
- **Rationale**: PokemonCard lives in core:designsystem and is used by 3 feature modules. Making it accept a SharedTransitionScope would couple the design system module to the shared element API and require all callers to provide a scope. Instead, adding an optional imageModifier: Modifier = Modifier parameter allows each screen to pass a modifier with shared element properties, keeping PokemonCard agnostic about the animation system.
- **Alternatives considered**:
  - Pass SharedTransitionScope as a parameter — Tightly couples the design system to the animation API. Violates separation of concerns.
  - Wrap the entire card externally — Possible but awkward; the shared element needs to be on the image specifically, not the card wrapper.

## Decision 5: Scope of shared elements

- **Decision**: Apply shared element transition to the image only (not the name text or other elements).
- **Rationale**: The image is the most impactful visual element to animate. Animating the name text would require passing it through the route and synchronizing with the top bar — added complexity for minimal UX value. The non-image content (stats, types, description) will use the existing slide/fade transitions from Navigation Compose.
- **Alternatives considered**:
  - Animate both image and name — More complex, requires name in route params and matching shared element keys on the top bar. Can be a future enhancement.
  - Animate image + card container — The card layout differs significantly from the detail layout; morphing the container would look unnatural.

## Decision 6: SharedTransitionLayout placement

- **Decision**: Place SharedTransitionLayout inside PokedexNavHost.kt, wrapping the AppNavHost call.
- **Rationale**: SharedTransitionLayout must be an ancestor of all composables that participate in shared element transitions. Since PokedexNavHost defines all routes and is the single place where navigation happens, placing it here ensures all route transitions can use shared elements. The AppNavHost wrapper in core:navigation remains thin and unchanged.
- **Alternatives considered**:
  - Place in MainActivity — Too far up the tree; would need to thread the scope down through PokedexNavHost.
  - Modify AppNavHost to include it — Would couple the core navigation module to the animation API. Better to keep AppNavHost as a pure NavHost wrapper.
