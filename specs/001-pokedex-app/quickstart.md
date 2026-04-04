# Quickstart: Pokedex App

## Prerequisites

- Android Studio Ladybug+ (or latest stable)
- JDK 17+
- Android SDK 35 (target), SDK 26 (min)
- Kotlin 2.0+

## Setup

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd pokedata
   git checkout 001-pokedex-app
   ```

2. **Sync Gradle**
   ```bash
   ./gradlew sync
   ```
   Or open in Android Studio and click "Sync Project with Gradle Files".

3. **Run the app**
   ```bash
   ./gradlew :app:installDebug
   ```
   Or run the `:app` configuration from Android Studio on an emulator or physical device.

## Module Structure

```
:app                        — Entry point (Application class, MainActivity)
:feature:pokemon-list       — Paginated Pokemon list
:feature:pokemon-detail     — Pokemon detail screen
:feature:search             — Text search
:feature:favorites          — Favorites management
:core:data                  — Retrofit, Room, shared repositories
:core:designsystem          — M3 theme, colors, typography
:core:navigation            — Navigation graph, routes
:core:ui                    — Shared composables, extensions
:core:testing               — Test utilities, fakes
```

## Running Tests

```bash
# Unit tests (all modules)
./gradlew test

# Unit tests for specific module
./gradlew :feature:pokemon-list:test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint
```

## Key Architecture Notes

- **MVVM + Clean**: Each feature has `presentation/`, `domain/`, `data/` layers
- **Koin DI**: Modules defined per feature and core layer in `di/` packages
- **Offline-first**: Room is source of truth; network refreshes cache
- **Paging 3**: List uses `RemoteMediator` for network + Room pagination
- **Navigation Compose**: Sealed class routes in `:core:navigation`

## Common Tasks

### Add a new feature module

1. Create module with convention plugin: `feature-module.gradle.kts`
2. Add to `settings.gradle.kts`
3. Add route in `:core:navigation`
4. Register Koin modules in `:app`

### Add a new API endpoint

1. Add method to `PokemonApi` in `:core:data`
2. Create DTO in `:core:data:remote:dto`
3. Create Repository method
4. Create UseCase in feature module
5. Wire ViewModel → UseCase → Repository

## Troubleshooting

- **Build fails**: Ensure JDK 17+ is configured in Android Studio
- **Tests fail on CI**: Check emulator image and API level match config
- **No data loads**: Verify internet permission in manifest, check PokeAPI availability
- **Images not loading**: Verify Coil dependency, check sprite URLs from API
