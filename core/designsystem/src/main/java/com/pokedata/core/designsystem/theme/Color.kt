package com.pokedata.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Light theme colors - Modern M3 tonal palettes with Pokemon-themed primary red
val LightPrimary = Color(0xFFB3261E)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFFFDAD5)
val LightOnPrimaryContainer = Color(0xFF410001)
val LightSecondary = Color(0xFF775651)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFFDAD5)
val LightOnSecondaryContainer = Color(0xFF2C1512)
val LightTertiary = Color(0xFF715C2E)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFDE0A6)
val LightOnTertiaryContainer = Color(0xFF261A00)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD5)
val LightOnErrorContainer = Color(0xFF410001)
val LightBackground = Color(0xFFFFFBFF)
val LightOnBackground = Color(0xFF201A19)
val LightSurface = Color(0xFFFFFBFF)
val LightOnSurface = Color(0xFF201A19)
val LightSurfaceVariant = Color(0xFFF5DDDA)
val LightOnSurfaceVariant = Color(0xFF534341)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFFFF8F7)
val LightSurfaceContainer = Color(0xFFFFF1EE)
val LightSurfaceContainerHigh = Color(0xFFFFEBE8)
val LightSurfaceContainerHighest = Color(0xFFFFDAD5)
val LightOutline = Color(0xFF857370)
val LightOutlineVariant = Color(0xFFD8C2BF)
val LightInverseSurface = Color(0xFF362F2E)
val LightInverseOnSurface = Color(0xFFFBEEEC)
val LightInversePrimary = Color(0xFFFFB4A9)

// Dark theme colors - Modern M3 tonal palettes
val DarkPrimary = Color(0xFFFFB4A9)
val DarkOnPrimary = Color(0xFF690003)
val DarkPrimaryContainer = Color(0xFF930006)
val DarkOnPrimaryContainer = Color(0xFFFFDAD5)
val DarkSecondary = Color(0xFFE7BDB6)
val DarkOnSecondary = Color(0xFF442925)
val DarkSecondaryContainer = Color(0xFF5D3F3A)
val DarkOnSecondaryContainer = Color(0xFFFFDAD5)
val DarkTertiary = Color(0xFFDFC38D)
val DarkOnTertiary = Color(0xFF3E2E05)
val DarkTertiaryContainer = Color(0xFF57441A)
val DarkOnTertiaryContainer = Color(0xFFFDE0A6)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD5)
val DarkBackground = Color(0xFF201A19)
val DarkOnBackground = Color(0xFFEDE0DD)
val DarkSurface = Color(0xFF201A19)
val DarkOnSurface = Color(0xFFEDE0DD)
val DarkSurfaceVariant = Color(0xFF534341)
val DarkOnSurfaceVariant = Color(0xFFD8C2BF)
val DarkSurfaceContainerLowest = Color(0xFF1B1413)
val DarkSurfaceContainerLow = Color(0xFF281F1E)
val DarkSurfaceContainer = Color(0xFF2D2322)
val DarkSurfaceContainerHigh = Color(0xFF382D2C)
val DarkSurfaceContainerHighest = Color(0xFF443836)
val DarkOutline = Color(0xFFA08D8A)
val DarkOutlineVariant = Color(0xFF534341)
val DarkInverseSurface = Color(0xFFEDE0DD)
val DarkInverseOnSurface = Color(0xFF362F2E)
val DarkInversePrimary = Color(0xFFB3261E)

// Pokemon type colors for display
object PokemonTypeColors {
    val Normal = Color(0xFFA8A878)
    val Fire = Color(0xFFF08030)
    val Water = Color(0xFF6890F0)
    val Electric = Color(0xFFF8D030)
    val Grass = Color(0xFF78C850)
    val Ice = Color(0xFF98D8D8)
    val Fighting = Color(0xFFC03028)
    val Poison = Color(0xFFA040A0)
    val Ground = Color(0xFFE0C068)
    val Flying = Color(0xFFA890F0)
    val Psychic = Color(0xFFF85888)
    val Bug = Color(0xFFA8B820)
    val Rock = Color(0xFFB8A038)
    val Ghost = Color(0xFF705898)
    val Dragon = Color(0xFF7038F8)
    val Dark = Color(0xFF705848)
    val Steel = Color(0xFFB8B8D0)
    val Fairy = Color(0xFFEE99AC)

    fun getColor(typeName: String): Color {
        return when (typeName.lowercase()) {
            "normal" -> Normal
            "fire" -> Fire
            "water" -> Water
            "electric" -> Electric
            "grass" -> Grass
            "ice" -> Ice
            "fighting" -> Fighting
            "poison" -> Poison
            "ground" -> Ground
            "flying" -> Flying
            "psychic" -> Psychic
            "bug" -> Bug
            "rock" -> Rock
            "ghost" -> Ghost
            "dragon" -> Dragon
            "dark" -> Dark
            "steel" -> Steel
            "fairy" -> Fairy
            else -> Normal
        }
    }
}

fun Color.contrastingTextColor(): Color {
    val luminance = red * 0.299f + green * 0.587f + blue * 0.114f
    return if (luminance > 0.5f) Color.Black else Color.White
}
