package com.challenge.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val darkColorScheme = darkColors(
    primary = Purple80,
    secondary = PurpleGrey80
)

private val lightColorScheme = lightColors(
    primary = Purple40,
    secondary = PurpleGrey40,
    background = WhiteSmoke,
    surface = SatinBlue,
    onPrimary = WhiteSmoke,
    onSecondary = WhiteSmoke,
    onBackground = WhiteSmoke,
    onSurface = SatinBlue
)

@Composable
fun StarlingBankAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val starlingColorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colors = starlingColorScheme,
        shapes = starlingShapes,
        typography = StarlingTypography,
        content = content
    )
}