# API Contracts: PokeAPI Integration

## Overview

This document defines the API contracts between the Pokedex app and the PokeAPI (https://pokeapi.co/api/v2/).
These contracts serve as the interface specification for the Retrofit service layer.

**Base URL**: `https://pokeapi.co/api/v2/`
**Authentication**: None required
**Method**: GET only (consumption-only API)
**Fair Use**: Cache all responses locally. No rate limiting, but be respectful.

---

## Common Types

### NamedAPIResource

Reference object used across all endpoints.

```json
{
  "name": "bulbasaur",
  "url": "https://pokeapi.co/api/v2/pokemon/1/"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `name` | string | Resource name (lowercase, hyphenated) |
| `url` | string | Full URL to the resource detail endpoint |

### NamedAPIResourceList

Paginated list response for named endpoints.

```json
{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [ /* NamedAPIResource[] */ ]
}
```

| Field | Type | Description |
|-------|------|-------------|
| `count` | integer | Total number of resources available |
| `next` | string \| null | URL for next page |
| `previous` | string \| null | URL for previous page |
| `results` | NamedAPIResource[] | List of resources |

### Name

Localized name object.

```json
{
  "name": "Bulbasaur",
  "language": {
    "name": "en",
    "url": "https://pokeapi.co/api/v2/language/9/"
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `name` | string | Localized name |
| `language` | NamedAPIResource | Language reference |

---

## Contract 1: Pokemon List

**Endpoint**: `GET /pokemon`

**Purpose**: Retrieve a paginated list of all Pokemon (named list — only name + URL).

### Request

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `limit` | integer | No | 20 | Results per page (max 100) |
| `offset` | integer | No | 0 | Pagination offset |

### Response (200 OK) — `NamedAPIResourceList`

```json
{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [
    {
      "name": "bulbasaur",
      "url": "https://pokeapi.co/api/v2/pokemon/1/"
    },
    {
      "name": "ivysaur",
      "url": "https://pokeapi.co/api/v2/pokemon/2/"
    }
  ]
}
```

**Note**: The list endpoint returns only `name` and `url`. The Pokemon `id` must be extracted
from the URL (last path segment before trailing slash). Full Pokemon details require individual
detail endpoint calls.

### Error Responses

| Condition | Handling |
|-----------|----------|
| Network unavailable | Show cached data from Room or error state with retry |
| Timeout | Retry with exponential backoff (max 3 attempts) |
| 4xx/5xx | Show error state with retry action |

---

## Contract 2: Pokemon Detail

**Endpoint**: `GET /pokemon/{id or name}`

**Purpose**: Retrieve full details for a specific Pokemon.

### Request

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer or string | Yes | National dex number (e.g., `1`) or name (e.g., `bulbasaur`) |

### Response (200 OK) — `Pokemon`

```json
{
  "id": 1,
  "name": "bulbasaur",
  "base_experience": 64,
  "height": 7,
  "weight": 69,
  "is_default": true,
  "order": 1,
  "abilities": [
    {
      "is_hidden": false,
      "slot": 1,
      "ability": {
        "name": "overgrow",
        "url": "https://pokeapi.co/api/v2/ability/65/"
      }
    },
    {
      "is_hidden": true,
      "slot": 3,
      "ability": {
        "name": "chlorophyll",
        "url": "https://pokeapi.co/api/v2/ability/34/"
      }
    }
  ],
  "forms": [
    {
      "name": "bulbasaur",
      "url": "https://pokeapi.co/api/v2/pokemon-form/1/"
    }
  ],
  "sprites": {
    "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
    "front_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/1.png",
    "back_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/1.png",
    "back_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/shiny/1.png",
    "other": {
      "official-artwork": {
        "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
      }
    }
  },
  "stats": [
    {
      "base_stat": 45,
      "effort": 0,
      "stat": {
        "name": "hp",
        "url": "https://pokeapi.co/api/v2/stat/1/"
      }
    },
    {
      "base_stat": 49,
      "effort": 0,
      "stat": {
        "name": "attack",
        "url": "https://pokeapi.co/api/v2/stat/2/"
      }
    },
    {
      "base_stat": 49,
      "effort": 0,
      "stat": {
        "name": "defense",
        "url": "https://pokeapi.co/api/v2/stat/3/"
      }
    },
    {
      "base_stat": 65,
      "effort": 1,
      "stat": {
        "name": "special-attack",
        "url": "https://pokeapi.co/api/v2/stat/4/"
      }
    },
    {
      "base_stat": 65,
      "effort": 0,
      "stat": {
        "name": "special-defense",
        "url": "https://pokeapi.co/api/v2/stat/5/"
      }
    },
    {
      "base_stat": 45,
      "effort": 0,
      "stat": {
        "name": "speed",
        "url": "https://pokeapi.co/api/v2/stat/6/"
      }
    }
  ],
  "types": [
    {
      "slot": 1,
      "type": {
        "name": "grass",
        "url": "https://pokeapi.co/api/v2/type/12/"
      }
    },
    {
      "slot": 2,
      "type": {
        "name": "poison",
        "url": "https://pokeapi.co/api/v2/type/4/"
      }
    }
  ],
  "species": {
    "name": "bulbasaur",
    "url": "https://pokeapi.co/api/v2/pokemon-species/1/"
  },
  "moves": [ /* omitted — not used by app */ ],
  "game_indices": [ /* omitted — not used by app */ ],
  "held_items": [ /* omitted — not used by app */ ],
  "location_area_encounters": "https://pokeapi.co/api/v2/pokemon/1/encounters",
  "past_types": [ /* omitted — not used by app */ ]
}
```

### Fields Used by App

| Field | Type | Usage |
|-------|------|-------|
| `id` | integer | Primary identifier, display order |
| `name` | string | Display name (capitalize first letter) |
| `height` | integer | Display (in decimeters, divide by 10 for meters) |
| `weight` | integer | Display (in hectograms, divide by 10 for kg) |
| `sprites.front_default` | string \| null | List item image |
| `sprites.other.official-artwork.front_default` | string \| null | Detail screen high-res image |
| `stats[].base_stat` | integer | Stats chart (6 stats) |
| `stats[].stat.name` | string | Stat label (hp, attack, defense, special-attack, special-defense, speed) |
| `types[].slot` | integer | Type ordering (1 = primary, 2 = secondary) |
| `types[].type.name` | string | Type name for display and color mapping |
| `abilities[].ability.name` | string | Ability name for display |
| `abilities[].is_hidden` | boolean | Indicates hidden ability |
| `species.url` | string | URL to species endpoint for flavor text |

### Stat Name Mapping

| API `stat.name` | Display Label |
|-----------------|---------------|
| `hp` | HP |
| `attack` | Attack |
| `defense` | Defense |
| `special-attack` | Sp. Atk |
| `special-defense` | Sp. Def |
| `speed` | Speed |

### Error Responses

| Condition | Handling |
|-----------|----------|
| 404 | Pokemon not found — should not happen if navigating from list |
| Network unavailable | Show cached data from Room or error state with retry |

---

## Contract 3: Pokemon Species (Flavor Text)

**Endpoint**: `GET /pokemon-species/{id or name}`

**Purpose**: Retrieve flavor text descriptions for a Pokemon.

### Request

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer or string | Yes | National dex number or name |

### Response (200 OK) — `PokemonSpecies` (relevant fields only)

```json
{
  "id": 1,
  "name": "bulbasaur",
  "flavor_text_entries": [
    {
      "flavor_text": "A strange seed was\nplanted on its\nback at birth.\nThe plant sprouts\nand grows with\nthis POKéMON.",
      "language": {
        "name": "en",
        "url": "https://pokeapi.co/api/v2/language/9/"
      },
      "version": {
        "name": "red",
        "url": "https://pokeapi.co/api/v2/version/1/"
      }
    },
    {
      "flavor_text": "It can go for days\nwithout eating a\nsingle morsel.\nIn the bulb on\nits back, it\nstores energy.",
      "language": {
        "name": "en",
        "url": "https://pokeapi.co/api/v2/language/9/"
      },
      "version": {
        "name": "blue",
        "url": "https://pokeapi.co/api/v2/version/2/"
      }
    }
  ],
  "genera": [
    {
      "genus": "Seed Pokémon",
      "language": {
        "name": "en",
        "url": "https://pokeapi.co/api/v2/language/9/"
      }
    }
  ],
  "generation": {
    "name": "generation-i",
    "url": "https://pokeapi.co/api/v2/generation/1/"
  },
  "evolution_chain": {
    "url": "https://pokeapi.co/api/v2/evolution-chain/1/"
  },
  "color": {
    "name": "green",
    "url": "https://pokeapi.co/api/v2/pokemon-color/5/"
  }
}
```

### Fields Used by App

| Field | Type | Usage |
|-------|------|-------|
| `flavor_text_entries` | FlavorTextEntry[] | Pokemon description (filter by `language.name == "en"`) |
| `genera` | Genus[] | Pokemon category label (e.g., "Seed Pokémon") — filter by language |

### FlavorTextEntry

| Field | Type | Description |
|-------|------|-------------|
| `flavor_text` | string | Description text (may contain `\n` and `\f` — replace with spaces) |
| `language` | NamedAPIResource | Language of this entry |
| `version` | NamedAPIResource | Game version this entry is from |

**Note**: Flavor text may contain `\n` (newline) and `\f` (form feed) characters.
Replace `\f` with space and collapse multiple newlines for clean display.

### Error Responses

| Condition | Handling |
|-----------|----------|
| 404 | Species not found — show detail without description |
| Network unavailable | Use cached description from Room or omit description |

---

## Contract 4: Pokemon Type

**Endpoint**: `GET /type/{id or name}`

**Purpose**: Retrieve type details including damage relations (not required for v1 — type colors are mapped locally).

**Note for v1**: Type color mapping is handled locally in `:core:designsystem`. This endpoint is
not called in the initial version. Reserved for future expansion (type effectiveness, etc.).

---

## Retrofit Service Interface

```kotlin
interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonListResponse>

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): Response<PokemonDetailResponse>

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): Response<PokemonSpeciesResponse>
}
```

## DTO Mappings

### PokemonListResponse

```kotlin
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonSummary>
)

data class PokemonSummary(
    val name: String,
    val url: String  // Extract ID from URL: ".../pokemon/{id}/"
)
```

### PokemonDetailResponse

```kotlin
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSprites,
    val stats: List<PokemonStat>,
    val types: List<PokemonTypeEntry>,
    val abilities: List<PokemonAbilityEntry>,
    val species: NamedApiResource
)

data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)

data class PokemonStat(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedApiResource
)

data class PokemonTypeEntry(
    val slot: Int,
    val type: NamedApiResource
)

data class PokemonAbilityEntry(
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val ability: NamedApiResource
)

data class NamedApiResource(
    val name: String,
    val url: String
)
```

### PokemonSpeciesResponse

```kotlin
data class PokemonSpeciesResponse(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    val genera: List<GenusEntry>
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)

data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)
```

## Caching Strategy

- **OkHttp HTTP cache**: Cache all successful GET responses. Use `Cache-Control` headers.
- **Room persistent cache**: All fetched data stored in Room for offline access.
- **Fair Use Policy**: PokeAPI requires local caching of all responses.
- **No rate limiting**: API has no rate limits, but be respectful of hosting costs.

## Error Handling

| Scenario | Strategy |
|----------|----------|
| No network + no cache | Show error state with retry button |
| No network + stale cache | Show cached data with snackbar indicating stale data |
| 404 | Log and show appropriate empty state |
| 5xx | Retry with exponential backoff (max 3 attempts, base 1s) |
| Timeout (30s) | Retry once, then show error state |
| Malformed response | Log error, show generic error message |
