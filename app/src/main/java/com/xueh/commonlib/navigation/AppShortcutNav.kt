package com.xueh.commonlib.navigation

import android.content.Intent
import androidx.navigation3.runtime.NavKey

internal const val LauncherShortcutScheme = "commonlib"
internal const val LauncherShortcutHost = "shortcut"

/**
 * 解析桌面静态快捷方式发起的 [Intent.ACTION_VIEW]（`commonlib://shortcut/...`）。
 */
fun resolveLauncherShortcutNavKey(intent: Intent?): NavKey? {
    if (intent?.action != Intent.ACTION_VIEW) return null
    val uri = intent.data ?: return null
    if (uri.scheme != LauncherShortcutScheme || uri.host != LauncherShortcutHost) return null
    val segment = uri.pathSegments.firstOrNull() ?: return null
    return when (segment) {
        "material3" -> DemoMaterial3Playground
        "media3" -> DemoMedia3ExoPlayer
        "dialog" -> DemoDialog
        else -> null
    }
}
