package com.me.newsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = BlueLight,
    primaryVariant = BlurpleLight,
    secondary = GreenLight,
    onSecondary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onSurface = TextLight,
    onBackground = TextLight,
    error = RedLight
)

private val DarkColorPalette = darkColors(
    primary = BlueDark,
    primaryVariant = BlurpleDark,
    secondary = GreenDark,
    onSecondary = Color.Black,
    background = GreyDark,
    surface = NotQuiteBlackDark,
    onSurface = TextDark,
    onBackground = TextDark,
    error = RedDark
)

@Composable
fun NewsAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}