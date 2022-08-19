package com.xueh.comm_core.base.compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.Utils

data class AppThemeState(
    var darkTheme: Boolean = false,//是否是黑暗主题
    var appThemeColorType: AppThemeColorType = AppThemeColorType.GREEN,
)

val appThemeState by lazy { mutableStateOf(AppThemeState()) }

enum class AppThemeColorType {
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER, DEF
}

