# Feature Specification: Adaptive Layout (Large Screen, Foldable & Small Screen Support)

**Feature Branch**: `005-adaptive-layout`  
**Created**: 2026-04-25  
**Status**: Draft  
**Input**: User description: "Gostaria que o app Tambem desse suporte a dispositivos de tela grande e dobraveis, alem de devices com a tela pequena. Otimizando ao maximo a qualidade de usabilidade do usuario"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Adaptive Collection Browsing Across Screen Sizes (Priority: P1)

A user opens the Pokedex app on any device — a compact phone, a large tablet, or a desktop-class screen. The Pokemon list, search results, and favorites screens automatically adapt their layout density: a single-column scrolling list on narrow screens and a multi-column grid on wider screens, ensuring every pixel is used effectively without wasted space or the need for excessive scrolling.

**Why this priority**: This is the foundational adaptation that affects every screen in the app. Without it, even the most basic browsing experience feels broken on tablets (single-column lists stretch to absurd widths). It delivers immediate visual and usability improvement across all device sizes with the least complexity.

**Independent Test**: Can be fully tested by opening the Pokemon list, search results, and favorites on a tablet emulator/device — verifying items appear in a multi-column grid instead of a single-column list. On a phone, the existing single-column list behavior is preserved.

**Acceptance Scenarios**:

1. **Given** a user on a device with screen width >= 600dp (tablet/large), **When** they open the Pokemon list screen, **Then** Pokemon cards are displayed in a multi-column grid (3+ columns) that adapts to the available width, with no empty gaps or stretched cards.
2. **Given** a user on a device with screen width < 600dp (phone), **When** they open the Pokemon list screen, **Then** Pokemon cards are displayed in a single-column list, preserving the current compact layout.
3. **Given** a user on a large screen device, **When** they perform a search, **Then** search results display in a multi-column grid matching the Pokemon list grid layout.
4. **Given** a user on a large screen device, **When** they view their favorites, **Then** favorite Pokemon display in a multi-column grid matching the same adaptive layout.
5. **Given** a user on a tablet in portrait mode, **When** they rotate the device to landscape, **Then** the grid column count increases automatically to use the wider space without restart or data reload.

---

### User Story 2 - Master-Detail Split Pane on Large Screens (Priority: P2)

A user browsing on a tablet can see the Pokemon list on one side and instantly view details for the selected Pokemon on the other side — no navigation back-and-forth needed. Selecting a different Pokemon in the list immediately updates the detail pane, creating a fluid browsing experience that takes full advantage of the extra screen real estate.

**Why this priority**: This is the hallmark tablet/desktop experience that transforms the app from "a phone app running on a tablet" into a proper large-screen application. It eliminates constant navigation push/pop cycles and dramatically speeds up browsing on larger devices.

**Independent Test**: Can be tested by opening the app on a tablet (>= 840dp width) — the Pokemon list and detail appear side-by-side in a two-pane layout. Tapping different Pokemon in the list updates the detail pane without any navigation transition.

**Acceptance Scenarios**:

1. **Given** a user on a device with screen width >= 840dp, **When** they open the Pokemon list, **Then** the screen shows a two-pane layout: Pokemon list on the left/leading pane and the detail of the first (or last-viewed) Pokemon on the right/trailing pane.
2. **Given** a user on a device with screen width >= 840dp with the split pane visible, **When** they tap a different Pokemon in the list pane, **Then** the detail pane immediately updates to show that Pokemon's details with no navigation transition.
3. **Given** a user on a device with screen width >= 840dp with the split pane visible, **When** they rotate the device or fold/unfold, **Then** if the new width is >= 600dp but < 840dp, the split pane closes and the detail opens via full-screen navigation instead.
4. **Given** a user on a device with screen width >= 840dp in split pane, **When** they initiate a search from the search bar, **Then** search results replace the list pane content while the detail pane remains visible (and updates when a result is selected).
5. **Given** a user on a phone (width < 600dp), **When** they tap a Pokemon in the list, **Then** the detail screen opens via full-screen navigation (existing push-navigation behavior is preserved).

---

### User Story 3 - Adaptive Navigation Chrome (Priority: P3)

The app's main navigation adapts to the available screen space. On phones, a bottom navigation bar provides easy thumb access to the three main sections (Pokedex, Search, Favorites). On tablets and large screens, a side navigation rail replaces the bottom bar, providing a more natural navigation target closer to the content area and freeing vertical space for content.

**Why this priority**: This follows Material 3's recommended navigation patterns for different screen sizes. While the bottom bar works on phones, navigation rails are the design-standard pattern for tablets and large screens. This is a polish-level improvement that enhances the large-screen user experience.

**Independent Test**: Can be tested by launching the app on a tablet — a navigation rail appears on the side. On a phone, the existing bottom navigation bar is preserved.

**Acceptance Scenarios**:

1. **Given** a user on a device with screen width >= 600dp, **When** they open the app, **Then** a navigation rail is displayed on the leading edge (left in LTR) with icons for Pokedex, Search, and Favorites, replacing the bottom navigation bar.
2. **Given** a user on a device with screen width < 600dp, **When** they open the app, **Then** the existing bottom navigation bar is displayed (unchanged behavior).
3. **Given** a user on a large screen device with the navigation rail visible, **When** they tap a rail destination, **Then** the main content area updates to show the corresponding screen, while the rail highlights the active destination.
4. **Given** a user on a device that transitions from compact to expanded width (e.g., folding phone opened), **When** the width crosses the 600dp threshold, **Then** the navigation component seamlessly switches from bottom bar to navigation rail without disrupting the current screen or losing state.

---

### User Story 4 - Enhanced Detail Layout on Large Screens (Priority: P4)

When viewing a Pokemon's detail on a tablet or large screen (either in split pane or full-screen detail), the information is arranged in an intelligent multi-column layout: artwork and basic info on one side, abilities and base stats on the other — eliminating the need for excessive vertical scrolling and presenting all key information at a glance.

**Why this priority**: The detail screen contains a lot of information (artwork, types, description, abilities, stats). On large screens, a single scrolling column wastes the available width. A multi-column detail layout makes all key data visible without scrolling, improving comprehension and reducing interaction cost.

**Independent Test**: Can be tested by viewing a Pokemon detail on a tablet — the content is arranged in at least two columns, with artwork + basic info on one side and stats + abilities on the other.

**Acceptance Scenarios**:

1. **Given** a user on a device with screen width >= 600dp viewing a Pokemon detail, **When** the detail screen loads, **Then** content is arranged in a two-column layout: left column shows the Pokemon artwork, name, number, types, height/weight, and flavor description; right column shows base stats (with progress bars), abilities list, and evolution info if available.
2. **Given** a user on a phone (width < 600dp), **When** they view a Pokemon detail, **Then** the existing single-column scrolling layout is preserved.
3. **Given** a user on a device with screen width >= 840dp, **When** the detail is shown in the split-pane trailing pane (narrower), **Then** the detail layout gracefully condenses to a single column to fit the narrower pane while remaining readable.

---

### User Story 5 - Foldable & Posture-Aware Adaptation (Priority: P5)

A user with a foldable device (e.g., Galaxy Z Fold, Surface Duo) can fold or unfold their device while using the app, and the layout adapts smoothly to the new configuration. In tabletop/half-opened posture, the app respects the hinge/seam boundary so no content is hidden or cut off. In book posture (fully open), the app leverages the full inner screen with the same large-screen layouts described above.

**Why this priority**: Foldable device adoption is growing, and proper foldable support is expected by users of these premium devices. This ensures the app feels native on foldables rather than glitchy or unaware of the device's unique capabilities.

**Independent Test**: Can be tested using a foldable emulator — fold the device to various postures and verify layout adaptation and that no content is obscured by hinge/seam areas.

**Acceptance Scenarios**:

1. **Given** a user with a foldable device in closed/compact state (single small outer screen), **When** they use the app, **Then** the app displays phone-optimized layouts (single column, bottom navigation).
2. **Given** a user with a foldable device, **When** they unfold the device to reveal the large inner screen, **Then** the layout transitions smoothly to large-screen layouts (grid, split pane, navigation rail) without any data loss or visual glitches.
3. **Given** a user with a foldable device in tabletop/half-opened posture, **When** the app content is displayed, **Then** no interactive elements (buttons, text, images) are positioned across the hinge/seam boundary where they would be obscured or difficult to interact with.
4. **Given** a user with a dual-screen device (e.g., Surface Duo) spanning content across both screens, **When** the app displays information, **Then** the hinge gap between the two screens is respected — content is not split across the physical gap in a way that makes it unreadable.
5. **Given** a user actively using the app, **When** they fold or unfold the device, **Then** the current screen state (scroll position, selected item, entered text) is preserved through the layout transition.

---

### Edge Cases

- What happens when a tablet in split-pane mode rotates from landscape to portrait? The width may drop below 840dp — the split pane should close and the detail view should become full-screen, preserving the currently selected Pokemon.
- What happens when a user on a foldable has the app open in split pane (unfolded), then folds the device? The app must transition to phone layout, preserving the detail view state (now shown full-screen via navigation).
- What happens when the user resizes the app window in freeform multi-window mode (Android desktop/chromeOS)? Layout thresholds should respond dynamically as the window resizes.
- What happens when screen width is exactly at a threshold boundary (e.g., 599dp → 600dp)? The transition should be smooth — no flickering or repeated layout rebuilds near the threshold.
- What happens on very small screens (e.g., <= 320dp width, small wearable-sized devices)? The existing compact layouts must remain fully functional with no overflow or clipped content.
- What happens when the detail pane in split mode is too narrow for the multi-column detail layout? The detail content should condense to a single-column layout when the available pane width is below a readable threshold.
- What happens when the user has an active search query and the device configuration changes? The search query text and results must be preserved across the layout transition.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST detect screen width categories (compact: < 600dp, medium: 600-839dp, expanded: >= 840dp) and adapt layout accordingly across all screens.
- **FR-002**: System MUST display Pokemon list, search results, and favorites in a multi-column grid layout when screen width >= 600dp, with column count proportional to available width.
- **FR-003**: System MUST display Pokemon list, search results, and favorites in a single-column list layout when screen width < 600dp.
- **FR-004**: System MUST present a split (master-detail) pane layout on the Pokemon list screen when screen width >= 840dp, with the Pokemon list on one side and selected Pokemon detail on the other.
- **FR-005**: System MUST allow users to select different Pokemon in the list pane and see the detail pane update immediately without navigation transitions when in split-pane mode.
- **FR-006**: System MUST use a navigation rail component for the three main destinations (Pokedex, Search, Favorites) when screen width >= 600dp, replacing the bottom navigation bar.
- **FR-007**: System MUST use a bottom navigation bar for the three main destinations when screen width < 600dp.
- **FR-008**: System MUST arrange Pokemon detail content in a multi-column layout (artwork + basic info in one column, stats + abilities in another) when screen width >= 600dp and the detail is displayed in a full-width context.
- **FR-009**: System MUST gracefully transition layouts when screen width changes at runtime (rotation, fold/unfold, multi-window resize) without losing scroll position, selected items, entered text, or navigation state.
- **FR-010**: System MUST respect hinge/seam boundaries on foldable and dual-screen devices, ensuring no interactive or readable content is positioned across a physical gap or fold.
- **FR-011**: System MUST support landscape orientation on all screen sizes with properly adapted layouts.
- **FR-012**: System MUST ensure all interactive elements (buttons, cards, text fields) remain reachable and have minimum touch target sizes (48dp) on all screen sizes and configurations.
- **FR-013**: System MUST maintain the existing shared element transition animation when navigating from list to detail on compact screens (phones) — no regression in existing animation quality.
- **FR-014**: System MUST preserve accessibility features (content descriptions, focus order, semantic structure) across all adaptive layouts and screen configurations.

### Key Entities

- **Window Size Class**: Represents the device's current screen width category (compact, medium, expanded). Drives all layout adaptation decisions across the app.
- **Device Posture**: Represents the physical state of a foldable device (closed, half-opened/tabletop, fully open/book, tent). Influences layout boundaries and hinge-awareness.
- **Layout Configuration**: The active combination of layout parameters (column count, pane visibility, navigation component type) derived from the current window size class and posture.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users on tablet devices (>= 600dp width) can see at least 3x more Pokemon items on screen at once compared to phone layout, measured by visible card count in the list area.
- **SC-002**: Users on large-screen devices (>= 840dp width) can view a Pokemon's list entry and detail simultaneously without any navigation action — the detail pane updates within 500ms of selecting a different Pokemon.
- **SC-003**: The app transitions between layout configurations (rotation, fold/unfold, window resize) in under 300ms with no visible flickering or content flashing.
- **SC-004**: All interactive elements remain fully visible, accessible, and tappable across all supported screen size/form factor combinations — verified by manual QA on representative devices (small phone, large phone, small tablet, large tablet, foldable closed, foldable open).
- **SC-005**: 100% of existing functionality (search, favorites, offline browsing, type filtering) works identically in both compact and expanded layouts — no feature regressions introduced by the adaptive layout changes.
- **SC-006**: No content is rendered in a hinge/seam zone on foldable or dual-screen devices — verified by layout inspection on all supported foldable postures.

## Assumptions

- The project already includes Material 3 adaptive navigation suite libraries in its dependencies, which provide window size class detection and navigation rail components. These will be leveraged rather than built from scratch.
- Screen width thresholds (compact < 600dp, medium 600-839dp, expanded >= 840dp) follow Material 3 window size class conventions, which are the Android platform standard.
- Foldable device support focuses on hinge/seam awareness and dynamic layout transitions during folding/unfolding. Posture detection (tabletop, book, tent) uses platform-provided APIs available on devices running Android 12L+.
- The existing Paging 3 data loading, Room offline caching, and Koin dependency injection remain unchanged — this feature modifies only the presentation/UI layer.
- Small-screen optimization (the current phone layout) is already functional and serves as the baseline. This feature adds large-screen and foldable adaptations without degrading the existing compact experience.
- Freeform multi-window support (chromeOS, Android desktop mode) is included as part of the dynamic window resize handling, but is not a primary target.
- The two-pane split layout on tablets replaces the shared element transition for that specific screen configuration (transitions don't apply in side-by-side mode). The shared element animation continues to work on phones as before.
- The navigation destinations (Pokedex, Search, Favorites) remain unchanged at 3 main tabs.
