package com.xueh.comm_core.utils.compose

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.getInsetsController
import androidx.core.view.WindowInsetsCompat.Type.ime
import com.blankj.utilcode.util.BarUtils.setStatusBarColor
import com.google.accompanist.systemuicontroller.SystemUiController
import com.xueh.comm_core.base.compose.theme.appThemeState

/**
 * 设置为沉浸式状态栏
 */
fun Activity.transparentStatusBar() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

/**
 * 状态栏反色
 * 如果为 true，则将状态栏的前景色更改为浅色，以便可以清楚地阅读栏上的项目。如果为 false，则恢复为默认外观。
 * 此方法对 API < 23 没有影响
 */
fun Activity.setAndroidNativeLightStatusBar() {
    val controller = getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = !isDarkMode()
}

/**
 * 隐藏ime
 */
fun Activity?.hideIme() {
    if (this == null || window == null) return
    val controller = getInsetsController(window, window.decorView)
    controller.hide(ime())
}

/**
 * 显示ime
 */
fun Activity?.showIme() {
    if (this == null || window == null) return
    val controller = getInsetsController(window, window.decorView)
    controller.show(ime())
}


/**
 * 获取当前是否为深色模式
 * 深色模式的值为:0x21
 * 浅色模式的值为:0x11
 * @return true 为是深色模式   false为不是深色模式
 */
fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode == 0x21
}


/**
 * Set the status bar color.
 *
 * @param color The **desired** [Color] to set. This may require modification if running on an
 * API level that only supports white status bar icons.
 * @param darkIcons Whether dark status bar icons would be preferable. Only available on
 * API 23+.
 * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
 * dark icons were requested but are not available. Defaults to applying a black scrim.
 */
fun Activity.setStatusBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
) {
    val statusBarColor = when {
        darkIcons && Build.VERSION.SDK_INT < 23 -> transformColorForLightContent(color)
        else -> color
    }
    window.statusBarColor = statusBarColor.toArgb()
    if (Build.VERSION.SDK_INT >= 23) {
        @Suppress("DEPRECATION")
        if (darkIcons) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}


/**
 * Set the navigation bar color.
 *
 * @param color The **desired** [Color] to set. This may require modification if running on an
 * API level that only supports white navigation bar icons. Additionally this will be ignored
 * and [Color.Transparent] will be used on API 29+ where gesture navigation is preferred or the
 * system UI automatically applies background protection in other navigation modes.
 * @param darkIcons Whether dark navigation bar icons would be preferable. Only available on
 * API 26+.
 * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
 * dark icons were requested but are not available. Defaults to applying a black scrim.
 */
fun Activity.setNavigationBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
) {
    val navBarColor = when {
        Build.VERSION.SDK_INT >= 29 -> Color.Transparent // For gesture nav
        darkIcons && Build.VERSION.SDK_INT < 26 -> transformColorForLightContent(color)
        else -> color
    }
    window.navigationBarColor = navBarColor.toArgb()
    if (Build.VERSION.SDK_INT >= 26) {
        @Suppress("DEPRECATION")
        if (darkIcons) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
}

/**
 * Set the status and navigation bars to [color].
 *
 * @see setStatusBarColor
 * @see setNavigationBarColor
 */
fun Activity.setSystemBarsColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
) {
    setStatusBarColor(color, darkIcons, transformColorForLightContent)
    setNavigationBarColor(color, darkIcons, transformColorForLightContent)
}


/**
 * An [androidx.compose.Local] holding the current [SystemUiController] or throws an error if none
 * is [provided][androidx.compose.Providers].
 */
val LocalSystemUiController = staticCompositionLocalOf<SystemUiController> {
    error("No SystemUiController provided")
}

private val BlackScrim = Color(0f, 0f, 0f, 0.2f) // 20% opaque black
private val BlackScrimmed: (Color) -> Color = { original ->
    BlackScrim.compositeOver(original)
}