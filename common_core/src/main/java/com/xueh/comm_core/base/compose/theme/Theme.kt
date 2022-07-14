package com.xueh.comm_core.base.compose.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.xueh.comm_core.utils.DataStoreUtils

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CommonLibTheme( themeId: Int = 0,darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
//        LightColorPalette
//        getDefaultThemeId()
    }

    MaterialTheme(
//        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}


/////////////////////////////////////////////////////////////////////////////////////////
// 天蓝色
const val SKY_BLUE_THEME = 0
// 灰色
const val GRAY_THEME = 1
// 深蓝色
const val DEEP_BLUE_THEME = 2


///**
// * 主题状态
// */
//val themeTypeState: MutableState<Int> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//    mutableStateOf(getDefaultThemeId())
//}

/**
 * 获取当前默认主题
 */
//fun getDefaultThemeId(): Int = DataStoreUtils.getSyncData(CHANGED_THEME, SKY_BLUE_THEME)
//
//
///**
// * 通过主题 ID 来获取需要的主题
// */
//private fun getThemeForThemeId(themeId: Int) = when (themeId) {
//    SKY_BLUE_THEME -> {
//        playLightColors(
//            primary = primaryLight
//        )
//    }
//
//}

data class ThemeModel(val color: Color, val colorId: Int, val colorName: String)