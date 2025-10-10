package com.xueh.comm_core.base.compose.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.helper.compose.findActivity

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
fun AppBaseTheme(
    themeType: AppThemeType = appThemeType,    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // 禁用动态颜色, 这样无论设备运行的Android版本如何，都会使用你定义的颜色方案. 启用了动态颜色时, 会在 Android 12+ 上覆盖自定义颜色方案。
    content: @Composable () -> Unit,
) {
    val isDarkTheme=AppThemeType.isDark(themeType = themeType)
    val colors = if (isDarkTheme) darkThemeColors else lightThemeColors

    CompositionLocalProvider(
        LocalCustomColors provides colors,
//        LocalTextStyles provides AppBaseTheme.textStyle
    ) {
        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            isDarkTheme -> DarkGreenColorPalette
            else -> LightGreenColorPalette
        }

//        MaterialTheme(
//            colorScheme = colorScheme,
//            content = {
//                // 使用MaterialTheme.colorScheme获取颜色, 必须放在MaterialTheme之内.
//                val backgroundColor = MaterialTheme.colorScheme.background
//                val activity = LocalContext.current.findActivity()
//                val view = LocalView.current
//                val window = (view.context as Activity).window
//                // 设置状态栏
//                SideEffect {
//                    activity?.window?.statusBarColor = backgroundColor.toArgb()
//                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
//                }
//                content()
//            }
//        )

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

