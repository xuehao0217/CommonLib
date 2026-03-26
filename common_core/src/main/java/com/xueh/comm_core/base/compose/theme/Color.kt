package com.xueh.comm_core.base.compose.theme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** 主题调色板使用的颜色常量：绿/蓝/紫/橘等色系及通用色 */
val green200 = Color(0xffa5d6a7)
val green500 = Color(0xff4caf50)
val green700 = Color(0xff388e3c)

val blue500 = Color(0xFF3F51B5)
val blue200 = Color(0xFF9FA8DA)
val blue700 = Color(0xFF303F9F)

val purple200 = Color(0xFFB39DDB)
val purple = Color(0xFF833AB4)
val purple700 = Color(0xFF512DA8)

val orange200 = Color(0xFFff7961)
val orange500 = Color(0xFFf44336)
val orange700 = Color(0xFFba000d)


val teal200 = Color(0xff80deea)
val blue = Color(0xFF5851DB)


val RedBookRed = Color(0xFFFF2E4D)
val WhiteBackground = Color(0xFFFFFFFF)
val BlackBackground = Color(0xFF1F1D1D)

/**
 * 应用自定义主题色数据类
 * @param theme 主题主色
 * @param background 背景色
 * @param title 标题文字色
 * @param body 正文文字色
 * @param divider 分割线色
 */
@Immutable
data class AppThemeColors(
    val theme: Color,
    val background: Color,
    val title: Color,
    val body: Color,
    val divider: Color
)

/** 浅色主题配色：白底、黑色标题、灰色正文 */
val lightThemeColors = AppThemeColors(
    theme = RedBookRed,
    background = WhiteBackground,
    title = Color.Black,
    body = Color(0xFF666666),
    divider = Color.LightGray
)

/** 深色主题配色：黑底、白色标题、紫色主题色 */
val darkThemeColors = AppThemeColors(
    theme = purple200,
    background = BlackBackground,
    title = Color.White,
    body = Color(0xFF666666),
    divider = Color.DarkGray
)



