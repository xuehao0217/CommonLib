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
/////////////////////////AppThemeType（亮暗色模式）////////////////////////////////////
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

////////////////////////主题颜色类型（绿/紫/橘/蓝/壁纸动态色）////////////////////////////////////
var appThemeColorType by mutableStateOf(AppThemeColorType.GREEN)

enum class AppThemeColorType {
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER
}

//https://blog.csdn.net/wsyx768/article/details/138075205
@Composable
fun ComposeMaterialTheme(
    appColorType: AppThemeColorType = appThemeColorType,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = isThemeDark()
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



@Composable
fun isThemeDark():Boolean=AppThemeType.isDark(
    themeType = appThemeType
)


@Stable
@Composable
fun AppBaseTheme(content: @Composable () -> Unit) {
    val colors = if(isThemeDark())darkThemeColors else lightThemeColors
    CompositionLocalProvider(
        LocalCustomColors provides colors,
        LocalTextStyles provides AppTheme.textStyle
    ) {
        MaterialTheme(content = content)
    }
}


val LocalTextStyles = staticCompositionLocalOf { defaultTextStyle }
val LocalCustomColors = staticCompositionLocalOf { lightThemeColors }


///方便全局获取样式
object AppTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalCustomColors.current
    val textStyle: AppThemeTextStyle
        @Composable
        get() = LocalTextStyles.current
}
