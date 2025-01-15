package com.xueh.comm_core.base.compose.theme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


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

@Immutable
data class AppThemeColors(
    val theme: Color,
    val background: Color,
    val title: Color,
    val body: Color,
    val divider: Color
)

val lightThemeColors = AppThemeColors(
    theme = RedBookRed,
    background = WhiteBackground,
    title = Color.Black,
    body = Color(0xFF666666),
    divider = Color.LightGray
)

val darkThemeColors = AppThemeColors(
    theme = purple200,
    background = BlackBackground,
    title = Color.White,
    body = Color(0xFF666666),
    divider = Color.DarkGray
)



