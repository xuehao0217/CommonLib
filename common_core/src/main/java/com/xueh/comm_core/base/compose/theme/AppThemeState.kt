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
/** 全局亮暗色模式状态，支持跟随系统、浅色、深色三种模式 */
var appThemeType by mutableStateOf(AppThemeType.Light)

/**
 * 应用主题类型枚举：亮暗色模式
 * - [FOLLOW_SYSTEM] 跟随系统
 * - [Light] 浅色模式
 * - [Dark] 深色模式
 */
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

/**
 * 判断当前是否为深色主题
 * @return 若为深色模式或跟随系统且系统为深色则返回 true
 */
@Composable
fun isThemeDark():Boolean=AppThemeType.isDark(
    themeType = appThemeType
)

/** 全局主题色类型状态：绿/紫/橘/蓝/壁纸动态色 */
var appThemeColorType by mutableStateOf(AppThemeColorType.GREEN)

/**
 * 应用主题色类型枚举
 * - [PURPLE] 紫色
 * - [GREEN] 绿色
 * - [ORANGE] 橘色
 * - [BLUE] 蓝色
 * - [WALLPAPER] 壁纸动态色（Android 12+ 从系统壁纸提取）
 */
enum class AppThemeColorType {
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER
}

/**
 * Material3 主题选择流程：
 * 1. 根据 [appColorType] 与 [isThemeDark] 确定亮/暗色
 * 2. 绿/紫/橘/蓝使用预定义调色板
 * 3. WALLPAPER 在 Android 12+ 使用 dynamicColorScheme 从壁纸取色，否则回退为绿色
 * 4. 将选中的 ColorScheme 注入 MaterialTheme
 *
 * @param appColorType 主题色类型，默认使用全局 [appThemeColorType]
 * @param content 主题作用域内的可组合内容
 */
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

        AppThemeColorType.WALLPAPER ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                if (isDarkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            else
                if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


/**
 * 自定义主题系统入口：
 * 1. 根据 [isThemeDark] 选择 [lightThemeColors] 或 [darkThemeColors]
 * 2. 通过 CompositionLocalProvider 注入 LocalCustomColors 与 LocalTextStyles
 * 3. 子组件通过 [AppTheme.colors] / [AppTheme.textStyle] 获取当前主题
 *
 * @param content 主题作用域内的可组合内容
 */
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


/**
 * 全局主题访问对象，用于在任意可组合函数中获取当前主题色与文本样式
 * - [colors] 当前主题色（主题色、背景、标题、正文、分割线）
 * - [textStyle] 当前文本样式（标题/正文大中小）
 */
object AppTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalCustomColors.current
    val textStyle: AppThemeTextStyle
        @Composable
        get() = LocalTextStyles.current
}
