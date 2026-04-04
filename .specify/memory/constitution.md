<!--
SYNC IMPACT REPORT
==================
Version change: N/A → 1.0.0 (initial constitution)
Added principles:
  - I. Feature-First Modularity
  - II. MVVM + Clean Architecture
  - III. Compose-First UI
  - IV. Test-Guided Development
  - V. Offline-First Data
  - VI. Material 3 Design System
  - VII. Performance & Memory Discipline
Added sections:
  - Technology Stack
  - Development Workflow & Quality Gates
Removed sections: N/A
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ updated (Constitution Check + structure)
  - .specify/templates/tasks-template.md ✅ updated (path conventions for Android modular)
  - .specify/templates/spec-template.md ✅ no changes needed (technology-agnostic)
Follow-up TODOs: None
-->

# Pokedex Constitution

## Core Principles

### I. Feature-First Modularity

Every feature lives in its own module. The app uses a feature-module architecture where each
domain area is independently developable, testable, and replaceable.

- Feature modules follow the naming convention `:feature:<name>` (e.g., `:feature:pokemon-list`,
  `:feature:pokemon-detail`, `:feature:search`).
- Core/shared modules follow `:core:<name>` (e.g., `:core:ui`, `:core:data`, `:core:designsystem`,
  `:core:navigation`, `:core:testing`).
- Dependencies flow unidirectionally: feature modules depend on core modules, never on other
  feature modules. The `:app` module is the sole entry point that aggregates all features.
- Each feature module owns its Presentation (Compose screens + ViewModels), Domain (UseCases),
  and Data (Repository) layers — no cross-feature coupling.
- Adding a new feature requires a new module or a clearly justified addition to an existing one.

### II. MVVM + Clean Architecture

The app follows MVVM as the primary presentation pattern layered on Clean Architecture principles.

- **Presentation layer**: Jetpack Compose UI + ViewModels. ViewModels expose `StateFlow` or
  `SharedFlow` to composables. ViewModels MUST use `SavedStateHandle` for configuration-change
  survival.
- **Domain layer**: UseCases encapsulate business logic. They are pure Kotlin, free of Android
  framework dependencies, and testable with JUnit alone.
- **Data layer**: Repositories abstract data sources (Retrofit for network, Room for local cache).
  Repositories implement offline-first: serve cached data immediately, then refresh from network.
- Dependency injection via **Koin**. Modules are declared per feature and per core layer.
  Scopes: `single` for repositories/services, `viewModel` for ViewModels, `factory` for UseCases.
- Coroutines are structured: `viewModelScope` for UI-bound work, `Dispatchers.IO` for
  network/database, `Dispatchers.Default` for CPU-intensive transformations.
- Sealed classes model all UI states (e.g., `UiState<out T>` with `Loading`, `Success(T)`,
  `Error(Throwable)` subtypes).

### III. Compose-First UI

All user interfaces are built with Jetpack Compose. No XML layouts are permitted.

- Every screen is a `@Composable` function. Complex screens decompose into smaller, reusable
  composables.
- State flows top-down, events flow bottom-up. Composables receive state as parameters and emit
  events via lambda callbacks.
- Side effects use the appropriate Compose effect API: `LaunchedEffect` for coroutine-based
  side effects, `DisposableEffect` for cleanup, `produceState` for converting non-Compose state.
- Lists use `LazyColumn` / `LazyVerticalGrid` with `items` or `itemsIndexed`. Never use
  `Column` for scrollable lists of unknown size.
- Navigation uses **Navigation Compose** with a `NavHost`. Routes are defined as sealed classes
  or objects for type safety. Arguments use `navArgument` with explicit types.
- Images load via **Coil** (`AsyncImage`). Always provide `contentDescription` for accessibility
  and `placeholder`/`error` drawables for UX polish.
- Pagination uses **Paging 3**. `PagingSource` is implemented in the Repository layer.
  Composables consume `LazyPagingItems` via `collectAsLazyPagingItems()`.

### IV. Test-Guided Development

Tests are mandatory for all business logic and data-layer code. UI tests cover critical user
journeys.

- **Unit tests** (minimum 70% branch coverage on domain and data layers):
  - ViewModels tested with `runTest` and `UnconfinedTestDispatcher`.
  - UseCases tested as pure functions — no Android dependencies required.
  - Repositories tested with mocked network (MockWebServer) and in-memory Room databases.
- **UI tests** use the Compose Testing API (`createAndroidComposeRule`). Cover critical paths:
  screen rendering, user interactions, navigation, loading/error/empty states.
- **Integration tests** validate Repository behavior against real Room (in-memory) and stubbed
  API responses.
- Koin tests use KoinTest with mocked modules to verify injection graphs.
- Every PR MUST include tests for new features and bug fixes. Code without tests is incomplete.

### V. Offline-First Data

The app MUST function with partial or no network connectivity. Cached data is always preferred
over network calls when available and not stale.

- **Room** is the source of truth. The UI observes Room via `Flow`. Network responses update
  Room, which then emits to observers.
- Retrofit calls are wrapped in repository logic that writes to Room first, then fetches from
  network and updates.
- **Paging 3** integrates with Room via `PagingSource` for paginated lists (e.g., Pokemon list).
- Network errors are surfaced gracefully: show cached data with a snackbar indicating stale data,
  or show an error state with retry action if no cache exists.
- Database migrations are explicit and versioned. Every schema change requires a `Migration`
  object or a `fallbackToDestructiveMigration` with documented justification.
- DAO queries use `@Query` with parameters (never string concatenation). Use `@Relation` and
  `@Embedded` for complex object graphs.

### VI. Material 3 Design System

All UI components use Google's Material 3 design system. No custom widgets that duplicate M3
functionality.

- Theme wraps the app in `MaterialTheme` with custom `ColorScheme`, `Typography`, and `Shapes`
  derived from M3 tokens. Both light and dark themes are supported.
- Standard M3 components are used: `Scaffold`, `TopAppBar`, `BottomAppBar`/`NavigationBar`,
  `FloatingActionButton`, `Card` (Elevated/Filled/Outlined), `Chip`, `AlertDialog`,
  `Snackbar`, `TextField`/`OutlinedTextField`, `Button`/`IconButton`.
- Spacing uses M3 tokens: `4dp`, `8dp`, `12dp`, `16dp`, `24dp`, `32dp`. No arbitrary spacing
  values without documented rationale.
- Color contrast meets WCAG AA standards. All interactive elements have visible focus indicators.
- Animations use M3 defaults: `300ms` for transitions, `150ms` for micro-interactions.
  Easing curves follow M3 specifications (`FastOutSlowInEasing`, `LinearOutSlowInEasing`).

### VII. Performance & Memory Discipline

The app MUST deliver a smooth, responsive experience on mid-tier Android devices.

- **Cold start**: under 3 seconds on devices equivalent to Snapdragon 660.
- **Memory**: no leaks. All coroutines are scoped (`viewModelScope`, `lifecycleScope`). Coil
  caches are bounded. Flow collectors are cancelled on lifecycle destruction.
- **Lists**: `LazyColumn` / `LazyVerticalGrid` with `contentType` for heterogeneous items.
  Items are keyed by stable IDs (e.g., `pokemon.id`).
- **Images**: Coil with memory and disk caches. `AsyncImage` uses appropriate `contentScale`
  (`Crop`, `Fit`, or `Inside`). Large images are downsampled before loading.
- **Network**: OkHttp connection pooling, GZIP compression, and HTTP cache interceptor.
  Retry logic uses exponential backoff for transient failures (5xx, timeouts).
- **Database**: indexed queries on frequently accessed columns (`@Index`). Batch operations
  wrapped in `@Transaction`. Avoid loading entire tables into memory.

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| DI | Koin |
| Design System | Material 3 |
| Networking | Retrofit + OkHttp |
| Local Database | Room |
| Image Loading | Coil |
| Pagination | Paging 3 |
| Navigation | Navigation Compose |
| Coroutines | kotlinx.coroutines + Flow |
| Testing | JUnit, MockK, MockWebServer, Compose Testing, KoinTest |
| Build | Gradle (Kotlin DSL), KSP |

## Development Workflow & Quality Gates

- **Linting**: ktlint enforces Kotlin style. Runs on every PR. Violations block merge.
- **Build**: `./gradlew assembleDebug` must succeed on every commit.
- **Tests**: `./gradlew test` runs unit tests. `./gradlew connectedAndroidTest` runs instrumented
  tests. Both must pass before merge.
- **Code review**: All PRs require at least one approval. Reviewers verify constitution compliance.
- **Complexity tracking**: Any deviation from these principles requires documentation in the PR
  with justification and a plan to remediate.
- **Git workflow**: Feature branches from `main`. Branch naming: `<issue-number>-<short-desc>`.
  Squash merge with descriptive commit messages.

## Governance

This constitution supersedes all other development practices in this project. Amendments require:

1. A documented proposal with rationale and migration plan.
2. Approval from the project maintainer(s).
3. Update to this file with version increment and date.

**Versioning policy** follows semantic versioning:
- **MAJOR**: Backward-incompatible principle removals or redefinitions.
- **MINOR**: New principles added or existing guidance materially expanded.
- **PATCH**: Clarifications, wording improvements, typo fixes.

All PRs and code reviews MUST verify compliance with these principles. Complexity introduced
by any pattern or abstraction must be justified by concrete benefit.

**Version**: 1.0.0 | **Ratified**: 2026-04-03 | **Last Amended**: 2026-04-03
