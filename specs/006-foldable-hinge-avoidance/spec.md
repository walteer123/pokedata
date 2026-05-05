# Feature Specification: Foldable Hinge Avoidance

**Feature Branch**: `006-foldable-hinge-avoidance`
**Created**: 2026-05-04
**Status**: Draft
**Input**: User description: "Gostaria que, em um celular dobrável, a listagem dos itens não ficasse em cima da dobra(hinge) do dispositivo"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Avoiding Content Splitting on Foldables (Priority: P1)

A user with a foldable device opens the Pokedex app while the device is partially folded or unfolded. As they scroll through the Pokemon list, the content automatically avoids the physical hinge area, ensuring that no Pokemon card is split in half by the fold.

**Why this priority**: This is the core request and essential for basic usability and visual quality on foldable hardware.

**Independent Test**: Can be fully tested by using a foldable device emulator or physical device and verifying that list items are never bisected by the hinge.

**Acceptance Scenarios**:

1. **Given** a foldable device in a half-opened state, **When** the Pokemon list is displayed, **Then** a gap or padding is applied where the hinge is located, pushing items above or below the fold.
2. **Given** a foldable device transitioning from folded to unfolded, **When** the layout updates, **Then** the items shift smoothly to accommodate the new hinge position or its absence.

---

### Edge Cases

- What happens when the device is fully unfolded (flat)? The hinge avoidance should be disabled as there is no longer a physical obstruction.
- How does the system handle different foldable orientations (horizontal vs. vertical fold)? The avoidance must apply to the correct axis based on the device configuration.
- What happens if a single list item is larger than the available space between the hinge and the screen edge? The padding should be prioritized to ensure the item is not split.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST detect the presence and physical location of the device hinge.
- **FR-002**: System MUST automatically apply layout offsets or padding to list components to prevent content from overlapping the hinge area.
- **FR-003**: System MUST dynamically update the hinge avoidance area in real-time as the device folding angle changes.
- **FR-004**: System MUST ensure that interactive elements (buttons, click targets) within list items are not placed directly under the hinge where they would be difficult to press.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of list items in the Pokemon list are displayed fully on either side of the hinge without being bisected.
- **SC-002**: Zero instances of "unclickable" elements caused by overlap with the physical hinge.
- **SC-003**: Layout adjustments occur with no perceptible lag to the user during device folding/unfolding.

## Assumptions

- The device provides a standard API to report the bounding box or position of the physical hinge (e.g., WindowManager).
- The feature is primarily targeted at the main Pokemon listing, but will be implemented as a reusable pattern for other lists.
- The app is running on a version of the OS that supports foldable layout APIs.
