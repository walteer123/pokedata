# Tasks: Pokedex App

**Input**: Design documents from `/specs/001-pokedex-app/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/

**Tests**: Unit tests for ViewModels, UseCases, and Repositories included per constitution. UI tests for critical paths.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Android modular app**: Feature modules at `feature:<name>/src/main/java/...`, core modules at `core:<name>/src/main/java/...`
- **Presentation layer**: `feature:<name>/.../presentation/` (Composables + ViewModel)
- **Domain layer**: `feature:<name>/.../domain/` (UseCases)
- **Data layer**: `feature:<name>/.../data/` (Repository, DTOs, mappers)
- **Tests**: `feature:<name>/src/test/` (unit), `feature:<name>/src/androidTest/` (instrumented)
- **Core modules**: `core:ui/`, `core:data/`, `core:designsystem/`, `core:navigation/`, `core:testing/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, Gradle configuration, and module scaffolding

- [X] T001 Create root `build.gradle.kts` with Compose BOM, Kotlin 2.0+, and Android plugin versions
- [X] T002 [P] Create `settings.gradle.kts` with all module includes (`:app`, 4 feature modules, 5 core modules, `:build-logic`)
- [X] T003 [P] Create `build-logic/feature-module.gradle.kts` convention plugin (Compose, Koin, Coroutines, Testing deps)
- [X] T004 [P] Create `build-logic/core-module.gradle.kts` convention plugin (Kotlin, minimal deps)
- [X] T005 Create `:app` module with `MainActivity.kt`, `PokedexApplication.kt`, and AndroidManifest.xml
- [X] T006 [P] Create `gradle.properties` with Compose enabled, JVM args, and AndroidX settings
- [X] T007 [P] Create empty module directories with `build.gradle.kts` for all 9 modules + build-logic

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Core: Design System

- [X] T008 [P] Create `core:designsystem/theme/Color.kt` with M3 light/dark color schemes and Pokemon type color mapping (18 types)
- [X] T009 [P] Create `core:designsystem/theme/Type.kt` with M3 Typography scale (display, headline, title, body, label)
- [X] T010 Create `core:designsystem/theme/Theme.kt` wrapping `MaterialTheme` with custom ColorScheme, Typography, and Shapes
- [X] T011 [P] Create `core:designsystem/components/LoadingIndicator.kt` with M3 `CircularProgressIndicator`
- [X] T012 [P] Create `core:designsystem/components/ErrorState.kt` with error icon, message, and retry Button
- [X] T013 [P] Create `core:designsystem/components/EmptyState.kt` with illustration, title, subtitle, and optional CTA

### Core: Data Layer

- [X] T014 Create `core:data/remote/dto/` DTOs: `PokemonListResponse`, `PokemonSummary`, `PokemonDetailResponse`, `PokemonSprites`, `PokemonStat`, `PokemonTypeEntry`, `PokemonAbilityEntry`, `PokemonSpeciesResponse`, `FlavorTextEntry`, `GenusEntry`, `NamedApiResource`
- [X] T015 Create `core:data/remote/PokemonApi.kt` Retrofit interface with `getPokemonList`, `getPokemonDetail`, `getPokemonSpecies` suspend functions
- [X] T016 Create `core:data/local/entity/PokemonEntity.kt` Room entity with `@Entity(tableName = "pokemon")`, `@PrimaryKey`, `@ColumnInfo`, and `@Index` on `name` and `isFavorite`
- [X] T017 [P] Create `core:data/local/entity/PokemonTypeEntity.kt` Room entity with foreign key to `pokemon.id`
- [X] T018 [P] Create `core:data/local/entity/AbilityEntity.kt` Room entity with foreign key to `pokemon.id`
- [X] T019 [P] Create `core:data/local/entity/BaseStatsEntity.kt` Room entity with `pokemonId` as primary key
- [X] T020 Create `core:data/local/dao/PokemonDao.kt` with `@Query` for all, byId, search, favorites, insert, update, delete, and `PagingSource` for pagination
- [X] T021 [P] Create `core:data/local/dao/PokemonTypeDao.kt` with insert, deleteByPokemonId, getByPokemonId
- [X] T022 [P] Create `core:data/local/dao/AbilityDao.kt` with insert, deleteByPokemonId, getByPokemonId
- [X] T023 [P] Create `core:data/local/dao/BaseStatsDao.kt` with insert, deleteByPokemonId, getByPokemonId
- [X] T024 Create `core:data/local/PokemonDatabase.kt` Room `@Database` with all entities, DAOs, version 1, and `TypeConverters`
- [X] T025 Create `core:data/mapper/DtoToEntityMapper.kt` mapping `PokemonDetailResponse` → `PokemonEntity` + related entities
- [X] T026 Create `core:data/mapper/EntityToDomainMapper.kt` mapping `PokemonEntity` + relations → domain `Pokemon` model
- [X] T027 Create `core:data/repository/PokemonRepositoryImpl.kt` with offline-first: Room as source of truth, network refresh, `PagingSource` with `RemoteMediator`
- [X] T028 Create `core/data/di/DataModule.kt` Koin module providing Retrofit, OkHttp (with GZIP + cache interceptor), Room database, DAOs, and Repository

### Core: Navigation

- [X] T029 Create `core:navigation/Route.kt` sealed class with `PokemonList`, `PokemonDetail(pokemonId: Int)`, `Search`, and `Favorites` routes with type-safe route creation
- [X] T030 Create `core:navigation/AppNavHost.kt` with `NavHost`, `composable` destinations for each route, and `NavController` setup
- [X] T031 Create `core:navigation/di/NavigationModule.kt` Koin module providing `NavController` and navigation graph

### Core: UI Utilities

- [X] T032 [P] Create `core:ui/extensions/ModifierExtensions.kt` with common modifiers (clickable with ripple, noRippleClickable, shimmer loading)
- [X] T033 [P] Create `core:ui/extensions/StringExtensions.kt` with `capitalizeFirst()`, `formatHeight()`, `formatWeight()` utilities
- [X] T034 Create `core:ui/preview/PreviewAnnotations.kt` with `@PokedexPreview` combining light/dark theme and device previews

### Core: Testing

- [X] T035 [P] Create `core:testing/TestDispatcherRule.kt` with `UnconfinedTestDispatcher` and `runTest` helper
- [X] T036 [P] Create `core:testing/FakePokemonRepository.kt` implementing repository interface with in-memory data
- [X] T037 [P] Create `core:testing/TestData.kt` with sample Pokemon data (bulbasaur, charmander, etc.) for tests

**Checkpoint**: Foundation ready — user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Browse Pokemon List (Priority: P1) 🎯 MVP

**Goal**: Display a paginated, scrollable list of Pokemon with images, names, and numbers. Auto-load more on scroll. Support pull-to-refresh. Show loading, error, and empty states.

**Independent Test**: Open the app and verify a list of Pokemon loads with images, names, and numbers. Scrolling reveals more Pokemon. Pull-to-refresh updates the list.

### Tests for User Story 1

- [ ] T038 [P] [US1] Unit test `PokemonListViewModel` state transitions (Loading → Success, Loading → Error) in `feature:pokemon-list/src/test/.../PokemonListViewModelTest.kt`
- [ ] T039 [P] [US1] Unit test `PokemonListRepository` offline-first behavior with MockWebServer and in-memory Room in `feature:pokemon-list/src/test/.../PokemonListRepositoryTest.kt`
- [ ] T040 [P] [US1] Unit test `GetPokemonListUseCase` pagination logic in `feature:pokemon-list/src/test/.../GetPokemonListUseCaseTest.kt`
- [ ] T041 [US1] UI test `PokemonListScreen` renders list items, loading state, and error state in `feature:pokemon-list/src/androidTest/.../PokemonListScreenTest.kt`

### Implementation for User Story 1

- [X] T042 [P] [US1] Create domain model `PokemonListItem` (id, name, spriteUrl, isFavorite) in `feature:pokemon-list/domain/model/PokemonListItem.kt`
- [X] T043 [P] [US1] Create `GetPokemonListUseCase` returning `Flow<PagingData<PokemonListItem>>` in `feature:pokemon-list/domain/GetPokemonListUseCase.kt`
- [X] T044 [US1] Create `PokemonListRepository` with `RemoteMediator` implementation for Paging 3 in `feature:pokemon-list/data/PokemonListRepository.kt`
- [X] T045 [US1] Create `PokemonListPagingSource` extending `PagingSource<Int, PokemonListItem>` in `feature:pokemon-list/data/PokemonListPagingSource.kt`
- [X] T046 [US1] Create `PokemonListViewModel` with `StateFlow<PokemonListUiState>` and `LazyPagingItems` in `feature:pokemon-list/presentation/PokemonListViewModel.kt`
- [X] T047 [P] [US1] Create `PokemonListItem` composable (image, name, number, favorite icon) in `feature:pokemon-list/presentation/components/PokemonListItem.kt`
- [X] T048 [P] [US1] Create `PokemonListEmptyState` composable in `feature:pokemon-list/presentation/components/PokemonListEmptyState.kt`
- [X] T049 [US1] Create `PokemonListScreen` composable with `LazyColumn`, `collectAsLazyPagingItems()`, pull-to-refresh, and loading/error/empty states in `feature:pokemon-list/presentation/PokemonListScreen.kt`
- [X] T050 [US1] Create `PokemonListModule.kt` Koin module providing ViewModel, UseCase, and Repository in `feature:pokemon-list/di/PokemonListModule.kt`
- [X] T051 [US1] Wire `PokemonListScreen` as the start destination in `core:navigation/AppNavHost.kt`

**Checkpoint**: At this point, the app launches and displays a paginated Pokemon list with offline support. This is the MVP.

---

## Phase 4: User Story 2 - View Pokemon Details (Priority: P2)

**Goal**: Tap any Pokemon in the list to see full details: types (with color badges), abilities (with hidden indicator), base stats (visual chart), and flavor text description. Navigate back preserving list scroll position.

**Independent Test**: Tap any Pokemon from the list and verify a detail screen opens showing types, abilities, base stats, and description. Back navigation returns to the list.

### Tests for User Story 2

- [ ] T052 [P] [US2] Unit test `PokemonDetailViewModel` state transitions in `feature:pokemon-detail/src/test/.../PokemonDetailViewModelTest.kt`
- [ ] T053 [P] [US2] Unit test `GetPokemonDetailUseCase` with mocked repository in `feature:pokemon-detail/src/test/.../GetPokemonDetailUseCaseTest.kt`
- [ ] T054 [US2] UI test `PokemonDetailScreen` renders all sections (types, stats, abilities, description) in `feature:pokemon-detail/src/androidTest/.../PokemonDetailScreenTest.kt`

### Implementation for User Story 2

- [X] T055 [P] [US2] Create domain model `PokemonDetail` (id, name, height, weight, types, abilities, stats, description, spriteUrl) in `feature:pokemon-detail/domain/model/PokemonDetail.kt`
- [X] T056 [P] [US2] Create `GetPokemonDetailUseCase` returning `Flow<PokemonDetail>` in `feature:pokemon-detail/domain/GetPokemonDetailUseCase.kt`
- [X] T057 [US2] Create `PokemonDetailRepository` fetching from Room first, then network if stale, in `feature:pokemon-detail/data/PokemonDetailRepository.kt`
- [X] T058 [US2] Create `PokemonDetailViewModel` with `StateFlow<PokemonDetailUiState>` in `feature:pokemon-detail/presentation/PokemonDetailViewModel.kt`
- [X] T059 [P] [US2] Create `TypeBadge` composable with type-specific colors in `feature:pokemon-detail/presentation/components/TypeBadge.kt`
- [X] T060 [P] [US2] Create `StatsChart` composable with horizontal bar chart for 6 base stats in `feature:pokemon-detail/presentation/components/StatsChart.kt`
- [X] T061 [P] [US2] Create `AbilityList` composable showing abilities with hidden indicator in `feature:pokemon-detail/presentation/components/AbilityList.kt`
- [X] T062 [P] [US2] Create `PokemonDetailHeader` composable with large image and name in `feature:pokemon-detail/presentation/components/PokemonDetailHeader.kt`
- [X] T063 [US2] Create `PokemonDetailScreen` composable with `Scaffold`, scrollable `Column`, and all detail sections in `feature:pokemon-detail/presentation/PokemonDetailScreen.kt`
- [X] T064 [US2] Add `PokemonDetail` route to `core:navigation/Route.kt` with `pokemonId: Int` argument
- [X] T065 [US2] Add `PokemonDetail` composable destination to `core:navigation/AppNavHost.kt`
- [X] T066 [US2] Create `PokemonDetailModule.kt` Koin module in `feature:pokemon-detail/di/PokemonDetailModule.kt`
- [X] T067 [US2] Wire navigation click from `PokemonListItem` to `PokemonDetail` route with `pokemonId` in `feature:pokemon-list/presentation/PokemonListScreen.kt`

**Checkpoint**: User Stories 1 AND 2 both work independently. Full browse-to-detail flow is functional.

---

## Phase 5: User Story 3 - Manage Favorites (Priority: P3)

**Goal**: Mark/unmark Pokemon as favorites from the list or detail screen. View only favorited Pokemon as a filtered list. Favorite status persists across app sessions.

**Independent Test**: Favorite a Pokemon from the list, verify the UI updates (filled heart icon). Open favorites filter to see only favorited Pokemon. Remove a favorite and verify it disappears. Reopen the app and verify favorites persist.

### Tests for User Story 3

- [ ] T068 [P] [US3] Unit test `ToggleFavoriteUseCase` toggling logic in `feature:favorites/src/test/.../ToggleFavoriteUseCaseTest.kt`
- [ ] T069 [P] [US3] Unit test `GetFavoritesUseCase` returning only favorited Pokemon in `feature:favorites/src/test/.../GetFavoritesUseCaseTest.kt`
- [ ] T070 [P] [US3] Unit test `FavoritesViewModel` state management in `feature:favorites/src/test/.../FavoritesViewModelTest.kt`
- [ ] T071 [US3] UI test favorite/unfavorite interaction updates UI in `feature:favorites/src/androidTest/.../FavoritesScreenTest.kt`

### Implementation for User Story 3

- [X] T072 [P] [US3] Create `ToggleFavoriteUseCase` updating `isFavorite` in Room in `feature:favorites/domain/ToggleFavoriteUseCase.kt`
- [X] T073 [P] [US3] Create `GetFavoritesUseCase` returning `Flow<List<PokemonListItem>>` filtered by `isFavorite = true` in `feature:favorites/domain/GetFavoritesUseCase.kt`
- [X] T074 [P] [US3] Create `RemoveFavoriteUseCase` (alias for toggle off) in `feature:favorites/domain/RemoveFavoriteUseCase.kt`
- [X] T075 [US3] Add `updateFavoriteStatus(id: Int, isFavorite: Boolean)` to `PokemonDao` in `core:data/local/dao/PokemonDao.kt`
- [X] T076 [US3] Create `FavoritesViewModel` with `StateFlow<FavoritesUiState>` in `feature:favorites/presentation/FavoritesViewModel.kt`
- [X] T077 [P] [US3] Create `FavoritesScreen` composable with `LazyColumn` of favorited Pokemon in `feature:favorites/presentation/FavoritesScreen.kt`
- [X] T078 [US3] Add `Favorites` route to `core:navigation/Route.kt`
- [X] T079 [US3] Add `Favorites` destination to `core:navigation/AppNavHost.kt`
- [X] T080 [US3] Add favorites tab to bottom `NavigationBar` in `PokemonListScreen` or create `MainScreen` shell with bottom nav
- [X] T081 [US3] Add favorite toggle button to `PokemonListItem` composable (heart icon, filled/unfilled) in `feature:pokemon-list/presentation/components/PokemonListItem.kt`
- [X] T082 [US3] Add favorite toggle button to `PokemonDetailScreen` header in `feature:pokemon-detail/presentation/PokemonDetailScreen.kt`
- [X] T083 [US3] Create `FavoritesModule.kt` Koin module in `feature:favorites/di/FavoritesModule.kt`
- [X] T084 [US3] Register Koin modules in `PokedexApplication.kt`

**Checkpoint**: User Stories 1, 2, AND 3 all work independently. Favorites persist across sessions.

---

## Phase 6: User Story 4 - Search Pokemon by Text (Priority: P3)

**Goal**: Search for Pokemon by name or number. Results filter in real-time with debounced input (300ms). Show empty state when no results match. Clear search to restore full list.

**Independent Test**: Enter a Pokemon name in the search field and verify matching results appear. Clear the search and verify the full list is restored. Search for a non-existent term and verify the empty state is shown.

### Tests for User Story 4

- [ ] T085 [P] [US4] Unit test `SearchPokemonUseCase` with various queries in `feature:search/src/test/.../SearchPokemonUseCaseTest.kt`
- [ ] T086 [P] [US4] Unit test `SearchViewModel` debounce and state management in `feature:search/src/test/.../SearchViewModelTest.kt`
- [ ] T087 [US4] UI test search input filters results and shows empty state in `feature:search/src/androidTest/.../SearchScreenTest.kt`

### Implementation for User Story 4

- [X] T088 [P] [US4] Create `SearchPokemonUseCase` with debounced query returning `Flow<List<PokemonListItem>>` in `feature:search/domain/SearchPokemonUseCase.kt`
- [X] T089 [US4] Create `SearchRepository` querying Room with `LIKE` on `name` and exact match on `id` in `feature:search/data/SearchRepository.kt`
- [X] T090 [US4] Add `searchByNameOrId(query: String)` to `PokemonDao` in `core:data/local/dao/PokemonDao.kt`
- [X] T091 [US4] Create `SearchViewModel` with `StateFlow<SearchUiState>` and debounced input in `feature:search/presentation/SearchViewModel.kt`
- [X] T092 [P] [US4] Create `SearchBar` composable with `TextField`, clear button, and debounce in `feature:search/presentation/components/SearchBar.kt`
- [X] T093 [US4] Create `SearchScreen` composable with search bar, results list, and empty state in `feature:search/presentation/SearchScreen.kt`
- [X] T094 [US4] Add `Search` route to `core:navigation/Route.kt`
- [X] T095 [US4] Add `Search` destination to `core:navigation/AppNavHost.kt`
- [X] T096 [US4] Add search icon to top app bar in `PokemonListScreen` that navigates to `SearchScreen`
- [X] T097 [US4] Create `SearchModule.kt` Koin module in `feature:search/di/SearchModule.kt`

**Checkpoint**: All 4 user stories are now independently functional.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T098 [P] Add Compose previews for all standalone composables (`PokemonListItem`, `TypeBadge`, `StatsChart`, `ErrorState`, `EmptyState`)
- [ ] T099 Add `MainActivity` splash screen with app icon while data initializes
- [ ] T100 [P] Add ktlint configuration and pre-commit hook for code style enforcement
- [ ] T101 [P] Write README.md with setup instructions, architecture overview, and module diagram
- [ ] T102 Run full test suite (`./gradlew test`) and fix any failures
- [ ] T103 Run lint (`./gradlew lint`) and fix all warnings
- [ ] T104 [P] Add ProGuard/R8 rules for Retrofit, Room, and Koin in `proguard-rules.pro`
- [ ] T105 Verify cold start time < 3s on emulator with baseline profiling
- [ ] T106 Run `./gradlew assembleRelease` and verify APK/AAB builds successfully

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can proceed sequentially in priority order (P1 → P2 → P3 → P3)
  - US3 (Favorites) integrates with US1 (list shows favorite status)
  - US4 (Search) is independent of US2 and US3
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) — No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) — Navigation from US1 list
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) — Integrates with US1 (favorite icon on list items) and US2 (favorite button on detail)
- **User Story 4 (P3)**: Can start after Foundational (Phase 2) — Independent of other stories

### Within Each User Story

- Tests MUST be written before implementation
- Domain models before UseCases
- UseCases before Repositories
- Repositories before ViewModels
- ViewModels before Composables
- Core implementation before integration/navigation wiring

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T002, T003, T004, T006, T007)
- All Foundational tasks marked [P] can run in parallel within their sub-groups
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Domain models within a story marked [P] can run in parallel
- Composable components within a story marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "T038 Unit test PokemonListViewModel state transitions"
Task: "T039 Unit test PokemonListRepository offline-first behavior"
Task: "T040 Unit test GetPokemonListUseCase pagination logic"

# Launch all domain models for User Story 1:
Task: "T042 Create domain model PokemonListItem"

# Launch all UI components for User Story 1 together:
Task: "T047 Create PokemonListItem composable"
Task: "T048 Create PokemonListEmptyState composable"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (7 tasks)
2. Complete Phase 2: Foundational (30 tasks) — CRITICAL, blocks all stories
3. Complete Phase 3: User Story 1 (14 tasks)
4. **STOP and VALIDATE**: App launches, displays paginated Pokemon list, works offline with cached data
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 (Browse List) → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 (View Details) → Test independently → Deploy/Demo
4. Add User Story 3 (Manage Favorites) → Test independently → Deploy/Demo
5. Add User Story 4 (Search) → Test independently → Deploy/Demo
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Browse List)
   - Developer B: User Story 2 (View Details)
   - Developer C: User Story 3 (Favorites) + User Story 4 (Search)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story is independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
