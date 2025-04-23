package com.xueh.comm_core.base.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.blankj.utilcode.util.Utils

var appThemeType by mutableStateOf(AppThemeType.Light)
enum class AppThemeType {
    FOLLOW_SYSTEM, Light, Dark;
    companion object {
        fun formatTheme(theme: Int? = 1): AppThemeType {
            entries.forEach {
                if (it.ordinal == theme) {
                    return it
                }
            }
            return Light
        }

        @Composable
        fun isDark(themeType: AppThemeType): Boolean {
            return when (themeType) {
                FOLLOW_SYSTEM -> isSystemInDarkTheme()
                Light -> false
                Dark -> true
            }
        }
    }
}


var appThemeColorType by mutableStateOf(AppThemeColorType.GREEN)

enum class AppThemeColorType {
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER
}


@Composable
fun isThemeDark():Boolean=AppThemeType.isDark(
    themeType = appThemeType
)


//////////////////////////////////////////////////////////////////////////////////////////////////
//https://blog.csdn.net/wsyx768/article/details/138075205
@Composable
fun ComposeMaterialTheme(
    appThemeState: AppThemeType = appThemeType,
    appColorType: AppThemeColorType = appThemeColorType,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = AppThemeType.isDark(themeType = appThemeState)
    val context = LocalContext.current
    val colors = when (appColorType) {
        AppThemeColorType.GREEN -> if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
        AppThemeColorType.PURPLE -> if (isDarkTheme) DarkPurpleColorPalette else LightPurpleColorPalette
        AppThemeColorType.ORANGE -> if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
        AppThemeColorType.BLUE -> if (isDarkTheme) DarkBlueColorPalette else LightBlueColorPalette
        AppThemeColorType.WALLPAPER -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (isDarkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        else
            if (isDarkTheme)
                DarkGreenColorPalette
            else
                LightGreenColorPalette
    }
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@Stable
@Composable
fun AppBaseTheme(themeType: AppThemeType = appThemeType, content: @Composable () -> Unit) {
    val colors = if (AppThemeType.isDark(themeType = themeType)) darkThemeColors else lightThemeColors
    CompositionLocalProvider(
        LocalCustomColors provides colors,
        LocalTextStyles provides AppBaseTheme.textStyle
    ) {
        MaterialTheme(content = content)
    }
}

object AppBaseTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalCustomColors.current
    val textStyle: AppThemeTextStyle
        @Composable
        get() = LocalTextStyles.current
}

val LocalTextStyles = staticCompositionLocalOf { defaultTextStyle }
val LocalCustomColors = staticCompositionLocalOf { lightThemeColors }

