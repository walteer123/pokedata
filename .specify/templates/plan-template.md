# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Kotlin (latest stable)  
**Primary Dependencies**: Jetpack Compose, Koin, Retrofit, Room, Coil, Paging 3, Navigation Compose  
**Storage**: Room (local SQLite database)  
**Testing**: JUnit, MockK, MockWebServer, Compose Testing API, KoinTest  
**Target Platform**: Android (minSdk per project config)
**Project Type**: android-app (modular)  
**Performance Goals**: Cold start < 3s, 60fps UI, smooth scrolling for large lists  
**Constraints**: Offline-first, mid-tier device support, WCAG AA accessibility  
**Scale/Scope**: Pokedex app with Pokemon list, detail, search, and type browsing features

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [ ] **Feature-First Modularity**: Does this feature belong in its own `:feature:<name>` module, or extend an existing one? No cross-feature dependencies introduced.
- [ ] **MVVM + Clean Architecture**: Presentation (Compose + ViewModel), Domain (UseCases), and Data (Repository) layers are separated. ViewModel uses `SavedStateHandle` and exposes `StateFlow`/`SharedFlow`.
- [ ] **Compose-First UI**: All UI is `@Composable` functions. No XML layouts. Navigation uses Navigation Compose with type-safe routes. Images use Coil `AsyncImage`.
- [ ] **Test-Guided Development**: Unit tests for ViewModels, UseCases, and Repositories included. UI tests for critical paths. Minimum 70% branch coverage on domain/data layers.
- [ ] **Offline-First Data**: Room is source of truth. Repository serves cached data first, then refreshes from network. Paging 3 used for paginated lists.
- [ ] **Material 3 Design System**: All components use M3 (`Scaffold`, `TopAppBar`, `Card`, `Chip`, etc.). Spacing uses M3 tokens. Dark mode supported. WCAG AA contrast met.
- [ ] **Performance & Memory**: Cold start < 3s. Coroutines scoped to `viewModelScope`/`lifecycleScope`. `LazyColumn` for lists. Coil for images with caching. OkHttp pooling + GZIP enabled.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
app/                                    # Entry point module
├── feature:pokemon-list/               # Pokemon list screen
│   ├── src/main/java/.../presentation/ # Composables + ViewModel
│   ├── src/main/java/.../domain/       # UseCases
│   └── src/main/java/.../data/         # Repository
├── feature:pokemon-detail/             # Pokemon detail screen
├── feature:search/                     # Search feature
├── core:ui/                            # Shared UI utilities
├── core:data/                          # Shared data (Retrofit, Room, Koin modules)
├── core:designsystem/                  # M3 theme, tokens, shared components
├── core:navigation/                    # Navigation graph, route definitions
└── core:testing/                       # Shared test utilities

tests/
├── unit/                               # JUnit tests for ViewModels, UseCases, Repositories
├── ui/                                 # Compose UI tests
└── integration/                        # Repository integration tests
```

**Structure Decision**: Android modular architecture with feature modules (`:feature:<name>`)
and core shared modules (`:core:<name>`). Each feature module follows MVVM + Clean Architecture
with Presentation, Domain, and Data layers. The `:app` module is the sole entry point.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
