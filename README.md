# Pokedex App

A modular Android Pokedex app built with Jetpack Compose, fetching Pokemon data from the [PokeAPI](https://pokeapi.co/).

## Features

- **Browse Pokemon**: Paginated list with images, names, and numbers
- **Pokemon Details**: View types, abilities, base stats, and flavor text
- **Favorites**: Mark/unmark Pokemon as favorites with persistent storage
- **Search**: Real-time search by name or Pokemon number
- **Offline Support**: Previously loaded data available without internet

## Architecture

```
app/
├── feature/
│   ├── pokemon-list/       # Paginated Pokemon list
│   ├── pokemon-detail/     # Pokemon detail screen
│   ├── search/             # Text search functionality
│   └── favorites/          # Favorites management
├── core/
│   ├── data/               # Network, database, repositories
│   ├── designsystem/       # M3 theme and shared components
│   ├── navigation/         # Navigation graph and routes
│   ├── ui/                 # Shared UI utilities
│   └── testing/            # Test utilities and fakes
└── build-logic/            # Gradle convention plugins
```

### Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture (Presentation → Domain → Data)
- **DI**: Koin
- **Network**: Retrofit + OkHttp
- **Database**: Room
- **Image Loading**: Coil
- **Pagination**: Paging 3
- **Navigation**: Navigation Compose
- **Testing**: JUnit, MockK, Turbine, Compose Testing API

## Getting Started

### Requirements

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35 (target), SDK 26 (minimum)

### Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run all unit tests
./gradlew testDebugUnitTest

# Run lint checks
./gradlew lint

# Build release APK
./gradlew assembleRelease
```

### Module Dependencies

```
:app → :feature:* → :core:*
:feature:* → :core:data, :core:designsystem, :core:ui, :core:navigation
:core:data → Room, Retrofit, OkHttp
```

## License

MIT
