package com.pokedata.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    animationDuration: Int = 500,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val animatedColorScheme = ColorScheme(
        primary = animateColorAsState(colorScheme.primary, tween(animationDuration)).value,
        onPrimary = animateColorAsState(colorScheme.onPrimary, tween(animationDuration)).value,
        primaryContainer = animateColorAsState(colorScheme.primaryContainer, tween(animationDuration)).value,
        onPrimaryContainer = animateColorAsState(colorScheme.onPrimaryContainer, tween(animationDuration)).value,
        inversePrimary = animateColorAsState(colorScheme.inversePrimary, tween(animationDuration)).value,
        secondary = animateColorAsState(colorScheme.secondary, tween(animationDuration)).value,
        onSecondary = animateColorAsState(colorScheme.onSecondary, tween(animationDuration)).value,
        secondaryContainer = animateColorAsState(colorScheme.secondaryContainer, tween(animationDuration)).value,
        onSecondaryContainer = animateColorAsState(colorScheme.onSecondaryContainer, tween(animationDuration)).value,
        tertiary = animateColorAsState(colorScheme.tertiary, tween(animationDuration)).value,
        onTertiary = animateColorAsState(colorScheme.onTertiary, tween(animationDuration)).value,
        tertiaryContainer = animateColorAsState(colorScheme.tertiaryContainer, tween(animationDuration)).value,
        onTertiaryContainer = animateColorAsState(colorScheme.onTertiaryContainer, tween(animationDuration)).value,
        background = animateColorAsState(colorScheme.background, tween(animationDuration)).value,
        onBackground = animateColorAsState(colorScheme.onBackground, tween(animationDuration)).value,
        surface = animateColorAsState(colorScheme.surface, tween(animationDuration)).value,
        onSurface = animateColorAsState(colorScheme.onSurface, tween(animationDuration)).value,
        surfaceVariant = animateColorAsState(colorScheme.surfaceVariant, tween(animationDuration)).value,
        onSurfaceVariant = animateColorAsState(colorScheme.onSurfaceVariant, tween(animationDuration)).value,
        surfaceTint = animateColorAsState(colorScheme.surfaceTint, tween(animationDuration)).value,
        inverseSurface = animateColorAsState(colorScheme.inverseSurface, tween(animationDuration)).value,
        inverseOnSurface = animateColorAsState(colorScheme.inverseOnSurface, tween(animationDuration)).value,
        error = animateColorAsState(colorScheme.error, tween(animationDuration)).value,
        onError = animateColorAsState(colorScheme.onError, tween(animationDuration)).value,
        errorContainer = animateColorAsState(colorScheme.errorContainer, tween(animationDuration)).value,
        onErrorContainer = animateColorAsState(colorScheme.onErrorContainer, tween(animationDuration)).value,
        outline = animateColorAsState(colorScheme.outline, tween(animationDuration)).value,
        outlineVariant = animateColorAsState(colorScheme.outlineVariant, tween(animationDuration)).value,
        scrim = animateColorAsState(colorScheme.scrim, tween(animationDuration)).value,
        surfaceBright = animateColorAsState(colorScheme.surfaceBright, tween(animationDuration)).value,
        surfaceDim = animateColorAsState(colorScheme.surfaceDim, tween(animationDuration)).value,
        surfaceContainer = animateColorAsState(colorScheme.surfaceContainer, tween(animationDuration)).value,
        surfaceContainerHigh = animateColorAsState(colorScheme.surfaceContainerHigh, tween(animationDuration)).value,
        surfaceContainerHighest = animateColorAsState(colorScheme.surfaceContainerHighest, tween(animationDuration)).value,
        surfaceContainerLow = animateColorAsState(colorScheme.surfaceContainerLow, tween(animationDuration)).value,
        surfaceContainerLowest = animateColorAsState(colorScheme.surfaceContainerLowest, tween(animationDuration)).value
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = animatedColorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}
