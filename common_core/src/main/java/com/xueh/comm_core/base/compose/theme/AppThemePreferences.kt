package com.xueh.comm_core.base.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.xueh.comm_core.utils.DataStoreUtils

/**
 * 使用 DataStore 持久化 [appThemeType]、[appThemeColorType]。
 * 当前以 **枚举 name**（字符串）为主键，避免调整枚举顺序导致旧数据错配；仍读取旧版 **ordinal** 键以迁移。
 *
 * 在 [com.xueh.comm_core.base.BaseApplication] 中调用 [restore]；
 * 在界面根组合处调用 [PersistAppThemePreferencesEffect] 或在主题变更后调用 [AppThemePreferences.persist]。
 */
object AppThemePreferences {
    private const val KEY_THEME_NAME = "pref_app_theme_type_name"
    private const val KEY_COLOR_NAME = "pref_app_theme_color_type_name"
    /** 旧版仅存 ordinal，保留读取以兼容已安装用户 */
    private const val KEY_THEME_TYPE_LEGACY = "pref_app_theme_type_ordinal"
    private const val KEY_COLOR_TYPE_LEGACY = "pref_app_theme_color_type_ordinal"

    fun restore() {
        val themeName = DataStoreUtils.getSyncData(KEY_THEME_NAME, "")
        val themeLegacy = DataStoreUtils.getSyncData(KEY_THEME_TYPE_LEGACY, AppThemeType.Light.ordinal)
        appThemeType = AppThemeType.parsePersisted(themeName, themeLegacy)

        val colorName = DataStoreUtils.getSyncData(KEY_COLOR_NAME, "")
        val colorLegacy = DataStoreUtils.getSyncData(KEY_COLOR_TYPE_LEGACY, AppThemeColorType.GREEN.ordinal)
        appThemeColorType = AppThemeColorType.parsePersisted(colorName, colorLegacy)
    }

    fun persist() {
        DataStoreUtils.putSyncData(KEY_THEME_NAME, appThemeType.name)
        DataStoreUtils.putSyncData(KEY_COLOR_NAME, appThemeColorType.name)
    }
}

/** 在根 Composable 中调用，主题切换时写回 DataStore。 */
@Composable
fun PersistAppThemePreferencesEffect() {
    LaunchedEffect(appThemeType, appThemeColorType) {
        AppThemePreferences.persist()
    }
}
