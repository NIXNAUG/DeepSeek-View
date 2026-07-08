package com.deepseek.view.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepSeekBlue,
    onPrimary = Gray50,
    primaryContainer = DeepSeekBlueLight,
    secondary = Teal,
    onSecondary = Gray50,
    secondaryContainer = TealLight,
    tertiary = BalanceGreen,
    background = Gray50,
    onBackground = Gray900,
    surface = Gray50,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    outline = Gray300,
    error = ErrorRed,
    onError = Gray50
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepSeekBlueLight,
    onPrimary = DarkBackground,
    primaryContainer = DeepSeekBlueDark,
    secondary = TealLight,
    onSecondary = DarkBackground,
    secondaryContainer = TealDark,
    tertiary = BalanceGreen,
    background = DarkBackground,
    onBackground = Gray100,
    surface = DarkSurface,
    onSurface = Gray100,
    surfaceVariant = Gray900,
    onSurfaceVariant = Gray300,
    outline = Gray700,
    error = ErrorRed,
    onError = Gray50
)

@Composable
fun DeepSeekViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DeepSeekTypography,
        content = content
    )
}
