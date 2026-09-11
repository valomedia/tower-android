/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.theme

//
//  Theme.kt
//  Tower_Android
//

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.dynamiccolor.DynamicColor
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeTonalSpot

/**
 * Resolve this role against a scheme.
 *
 * @param scheme    The scheme holding the tonal palettes to pick the tone from.
 * @return          The color for this role.
 */
private fun DynamicColor.toColor(scheme: DynamicScheme) = Color(getArgb(scheme))

/**
 * Derive the color scheme for TOWER from `Verdigris`.
 *
 * `lightColorScheme` and `darkColorScheme` only fill in the roles they are handed, and Material 3
 * offers no way to expand a seed color into the remaining ones, so the tonal palettes are built
 * with material-color-utilities and every role is mapped explicitly.
 *
 * @param darkTheme Whether to derive the scheme for a dark background.
 * @return          The color scheme for TOWER.
 */
private fun towerColorScheme(darkTheme: Boolean): ColorScheme {
    val scheme = SchemeTonalSpot(
        sourceColorHct = Hct.fromInt(Verdigris.toArgb()),
        isDark = darkTheme,
        contrastLevel = 0.0
    )
    val colors = MaterialDynamicColors()

    return ColorScheme(
        primary = colors.primary().toColor(scheme),
        onPrimary = colors.onPrimary().toColor(scheme),
        primaryContainer = colors.primaryContainer().toColor(scheme),
        onPrimaryContainer = colors.onPrimaryContainer().toColor(scheme),
        inversePrimary = colors.inversePrimary().toColor(scheme),
        secondary = colors.secondary().toColor(scheme),
        onSecondary = colors.onSecondary().toColor(scheme),
        secondaryContainer = colors.secondaryContainer().toColor(scheme),
        onSecondaryContainer = colors.onSecondaryContainer().toColor(scheme),
        tertiary = colors.tertiary().toColor(scheme),
        onTertiary = colors.onTertiary().toColor(scheme),
        tertiaryContainer = colors.tertiaryContainer().toColor(scheme),
        onTertiaryContainer = colors.onTertiaryContainer().toColor(scheme),
        background = colors.background().toColor(scheme),
        onBackground = colors.onBackground().toColor(scheme),
        surface = colors.surface().toColor(scheme),
        onSurface = colors.onSurface().toColor(scheme),
        surfaceVariant = colors.surfaceVariant().toColor(scheme),
        onSurfaceVariant = colors.onSurfaceVariant().toColor(scheme),
        surfaceTint = colors.surfaceTint().toColor(scheme),
        inverseSurface = colors.inverseSurface().toColor(scheme),
        inverseOnSurface = colors.inverseOnSurface().toColor(scheme),
        error = colors.error().toColor(scheme),
        onError = colors.onError().toColor(scheme),
        errorContainer = colors.errorContainer().toColor(scheme),
        onErrorContainer = colors.onErrorContainer().toColor(scheme),
        outline = colors.outline().toColor(scheme),
        outlineVariant = colors.outlineVariant().toColor(scheme),
        scrim = colors.scrim().toColor(scheme),
        surfaceBright = colors.surfaceBright().toColor(scheme),
        surfaceDim = colors.surfaceDim().toColor(scheme),
        surfaceContainer = colors.surfaceContainer().toColor(scheme),
        surfaceContainerHigh = colors.surfaceContainerHigh().toColor(scheme),
        surfaceContainerHighest = colors.surfaceContainerHighest().toColor(scheme),
        surfaceContainerLow = colors.surfaceContainerLow().toColor(scheme),
        surfaceContainerLowest = colors.surfaceContainerLowest().toColor(scheme)
    )
}

/**
 * The color scheme TOWER uses on a light background.
 */
private val LightColorScheme = towerColorScheme(darkTheme = false)

/**
 * The color scheme TOWER uses on a dark background.
 */
private val DarkColorScheme = towerColorScheme(darkTheme = true)

/**
 * Design for TOWER.
 *
 * This will apply a `MaterialTheme` with a custom color scheme and typography for TOWER to its
 * content. The color scheme is derived from `Verdigris`, so the app keeps its own accent color
 * instead of following the color Android derives from the wallpaper.
 *
 * @param darkTheme Whether to apply a dark color theme.
 * @param content   The `Composable` to apply the theme to.
 */
@Composable
fun TowerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
