package com.pokedata.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonSummary>
)

@Serializable
data class PokemonSummary(
    val name: String,
    val url: String
)

@Serializable
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

@Serializable
data class PokemonSprites(
    @SerialName("front_default") val frontDefault: String?,
    val other: OtherSprites?
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: OfficialArtwork?
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default") val frontDefault: String?
)

@Serializable
data class PokemonStat(
    @SerialName("base_stat") val baseStat: Int,
    val stat: NamedApiResource
)

@Serializable
data class PokemonTypeEntry(
    val slot: Int,
    val type: NamedApiResource
)

@Serializable
data class PokemonAbilityEntry(
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val ability: NamedApiResource
)

@Serializable
data class PokemonSpeciesResponse(
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    val genera: List<GenusEntry>
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)

@Serializable
data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)

@Serializable
data class NamedApiResource(
    val name: String,
    val url: String
)
