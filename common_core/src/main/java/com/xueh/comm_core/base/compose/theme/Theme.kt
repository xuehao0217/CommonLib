package com.xueh.comm_core.base.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun ComposeMaterial3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appThemeColorState: AppThemeColorType = AppThemeColorType.DEF,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = when (appThemeColorState) {
        AppThemeColorType.GREEN, AppThemeColorType.DEF -> if (darkTheme) DarkGreenColorPalette else LightGreenColorPalette
        AppThemeColorType.PURPLE -> if (darkTheme) DarkPurpleColorPalette else LightPurpleColorPalette
        AppThemeColorType.ORANGE -> if (darkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
        AppThemeColorType.BLUE -> if (darkTheme) DarkBlueColorPalette else LightBlueColorPalette
        AppThemeColorType.WALLPAPER -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        else
            if (darkTheme)
                DarkGreenColorPalette
            else
                LightGreenColorPalette
    }
    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


@Composable
fun BaseComposeTheme(
    systemBarsColor: Color? = null,
    darkTheme: Boolean = com.xueh.comm_core.base.compose.theme.appThemeState.darkTheme,
    appThemeState: AppThemeState = com.xueh.comm_core.base.compose.theme.appThemeState,
    content: @Composable () -> Unit,
) {
    ComposeMaterial3Theme(
        darkTheme = appThemeState.darkTheme,
        appThemeColorState = appThemeState.appThemeColorType
    ) {
        rememberSystemUiController().run {
            setSystemBarsColor(
                systemBarsColor ?: androidx.compose.material3.MaterialTheme.colorScheme.background,
                darkIcons = darkTheme
            )
        }
        content()
    }
}


/////////////////////////////////////////////////////////////////////////////////////////
//黑暗 绿色
private val DarkGreenColorPalette = darkColorScheme(
    primary = green500,
    primaryContainer = green200,
    onPrimaryContainer = green700,
    secondary = teal200,
    secondaryContainer = green700,
    onSecondaryContainer = Color.Black,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    error = Color.Red,
)

//黑暗 紫色
private val DarkPurpleColorPalette = darkColorScheme(
    primary = purple200,
    primaryContainer = purple700,
    secondary = teal200,
    secondaryContainer = purple700,
    onSecondaryContainer = Color.White,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    error = Color.Red,
)

//黑暗蓝色
private val DarkBlueColorPalette = darkColorScheme(
    primary = blue200,
    primaryContainer = blue700,
    secondary = teal200,
    secondaryContainer = blue700,
    onSecondaryContainer = Color.White,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    error = Color.Red,
)

//黑暗 橘色
private val DarkOrangeColorPalette = darkColorScheme(
    primary = orange200,
    primaryContainer = orange700,
    secondary = teal200,
    secondaryContainer = orange700,
    onSecondaryContainer = Color.White,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    error = Color.Red,
)

// Light pallets
private val LightGreenColorPalette = lightColorScheme(
    primary = green500,
    primaryContainer = green200,
    onPrimaryContainer = green700,
    secondary = teal200,
    secondaryContainer = green700,
    onSecondaryContainer = Color.White,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black
)

private val LightPurpleColorPalette = lightColorScheme(
    primary = purple,
    primaryContainer = purple700,
    secondary = teal200,
    secondaryContainer = purple700,
    onSecondaryContainer = Color.White,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black,
)

private val LightBlueColorPalette = lightColorScheme(
    primary = blue500,
    primaryContainer = blue700,
    secondary = teal200,
    secondaryContainer = blue700,
    onSecondaryContainer = Color.White,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black,
)

private val LightOrangeColorPalette = lightColorScheme(
    primary = orange500,
    primaryContainer = orange700,
    secondary = teal200,
    secondaryContainer = orange700,
    onSecondaryContainer = Color.White,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black,
)
