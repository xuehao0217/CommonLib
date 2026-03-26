package com.xueh.comm_core.base.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.xueh.comm_core.utils.DataStoreUtils

/**
 * 使用 DataStore 持久化 [appThemeType]、[appThemeColorType]。
 * 在 [com.xueh.comm_core.base.BaseApplication] 中调用 [restore]；
 * 在界面根组合处调用 [PersistAppThemePreferencesEffect] 或在主题变更后调用 [AppThemePreferences.persist]。
 */
object AppThemePreferences {
    private const val KEY_THEME_TYPE = "pref_app_theme_type_ordinal"
    private const val KEY_COLOR_TYPE = "pref_app_theme_color_type_ordinal"

    fun restore() {
        val themeOrdinal = DataStoreUtils.getSyncData(KEY_THEME_TYPE, AppThemeType.Light.ordinal)
        appThemeType = AppThemeType.formatTheme(themeOrdinal)
        val colorOrdinal = DataStoreUtils.getSyncData(KEY_COLOR_TYPE, AppThemeColorType.GREEN.ordinal)
        val colors = AppThemeColorType.entries
        appThemeColorType = colors.getOrElse(
            colorOrdinal.coerceIn(0, colors.lastIndex),
        ) { AppThemeColorType.GREEN }
    }

    fun persist() {
        DataStoreUtils.putSyncData(KEY_THEME_TYPE, appThemeType.ordinal)
        DataStoreUtils.putSyncData(KEY_COLOR_TYPE, appThemeColorType.ordinal)
    }
}

/** 在根 Composable 中调用，主题切换时写回 DataStore。 */
@Composable
fun PersistAppThemePreferencesEffect() {
    LaunchedEffect(appThemeType, appThemeColorType) {
        AppThemePreferences.persist()
    }
}
