/**
 * 主题 **状态与入口**：[appThemeType]、[appThemeColorType]、[ComposeMaterialTheme]、[AppTheme]；
 * 与 `Color.kt` / `Type.kt` 中的调色板、文本样式配合使用。
 *
 * **[ComposeMaterialTheme]** 为唯一根主题（[BaseComposeActivity] 已使用）：同时提供 Material3 [ColorScheme]（品牌色/动态色）、
 * [AppTypography]、[Shapes]，以及 [LocalCustomColors] / [LocalTextStyles]（语义色 [AppThemeColors] 与 [defaultTextStyle]）。
 * 子界面直接使用 [MaterialTheme] 与 [AppTheme]，无需再套一层主题 Composable。
 */
package com.xueh.comm_core.base.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

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
        /** 由持久化恢复的 **ordinal** 解析；越界或未知时回退 [Light]。 */
        fun formatTheme(theme: Int? = 1): AppThemeType {
            val ordinal = (theme ?: Light.ordinal).coerceIn(0, entries.lastIndex)
            return entries.getOrElse(ordinal) { Light }
        }

        /**
         * 优先按 **枚举 name** 解析（与 [AppThemePreferences] 新存储一致）；失败或空串时用 [legacyOrdinal] 走 [formatTheme]（兼容旧版仅存 ordinal）。
         */
        fun parsePersisted(storedName: String, legacyOrdinal: Int): AppThemeType {
            if (storedName.isNotBlank()) {
                runCatching { valueOf(storedName) }.getOrNull()?.let { return it }
            }
            return formatTheme(legacyOrdinal)
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
fun isThemeDark(): Boolean = AppThemeType.isDark(
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
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER;

    companion object {
        /**
         * 优先按 **枚举 name** 解析；失败或空串时用 [legacyOrdinal] 做安全索引（兼容旧版 ordinal）。
         */
        fun parsePersisted(storedName: String, legacyOrdinal: Int): AppThemeColorType {
            if (storedName.isNotBlank()) {
                runCatching { valueOf(storedName) }.getOrNull()?.let { return it }
            }
            val idx = legacyOrdinal.coerceIn(0, entries.lastIndex)
            return entries.getOrElse(idx) { GREEN }
        }
    }
}

/**
 * Material3 主题选择流程：
 * 1. 根据 [appColorType] 与 [isThemeDark] 确定亮/暗色
 * 2. 绿/紫/橘/蓝使用预定义调色板
 * 3. WALLPAPER 在 Android 12+ 使用 dynamicColorScheme 从壁纸取色，否则回退为绿色
 * 4. 按亮暗提供 [lightThemeColors] 或 [darkThemeColors] 到 [LocalCustomColors]，[defaultTextStyle] 到 [LocalTextStyles]
 * 5. 将 [ColorScheme]、[AppTypography]、[Shapes] 注入单层 [MaterialTheme]
 *
 * @param appColorType 若为 null 则使用全局 [appThemeColorType]。必须在函数体内读取全局状态，
 * 否则仅写在默认参数里时，Compose 可能无法订阅 [appThemeColorType] 变化，导致调色板切换不生效。
 * @param content 主题作用域内的可组合内容
 */
@Composable
fun ComposeMaterialTheme(
    appColorType: AppThemeColorType? = null,
    content: @Composable () -> Unit,
) {
    val resolvedColorType = appColorType ?: appThemeColorType
    val isDarkTheme = isThemeDark()
    val context = LocalContext.current

    val colors = when (resolvedColorType) {
        AppThemeColorType.GREEN -> if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
        AppThemeColorType.PURPLE -> if (isDarkTheme) DarkPurpleColorPalette else LightPurpleColorPalette
        AppThemeColorType.ORANGE -> if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
        AppThemeColorType.BLUE -> if (isDarkTheme) DarkBlueColorPalette else LightBlueColorPalette

        AppThemeColorType.WALLPAPER ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isDarkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            } else {
                if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
            }
    }

    val semanticColors = if (isDarkTheme) darkThemeColors else lightThemeColors
    CompositionLocalProvider(
        LocalCustomColors provides semanticColors,
        LocalTextStyles provides defaultTextStyle,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            shapes = Shapes,
            content = content
        )
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
