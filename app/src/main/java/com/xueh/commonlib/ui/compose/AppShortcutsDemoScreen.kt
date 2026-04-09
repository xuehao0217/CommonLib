package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 桌面「应用快捷方式」静态配置说明（实现见 [com.xueh.commonlib.navigation.resolveLauncherShortcutNavKey] 与 `res/xml/app_shortcuts.xml`）。
 */
@Composable
fun AppShortcutsDemoScreen() {
    DemoScreenIntro(
        text = "在系统桌面长按本应用图标，应出现几条快捷方式，点击后直接打开对应演示页。\n\n" +
            "• Material3 汇演 — commonlib://shortcut/material3\n" +
            "• Media3 播放器 — commonlib://shortcut/media3\n" +
            "• Dialog 示例 — commonlib://shortcut/dialog\n\n" +
            "实现要点：在 LAUNCHER Activity 上声明 meta-data android.app.shortcuts；" +
            "[androidx.core.content.pm.ShortcutManagerCompat] / 动态快捷方式可在此基础上扩展。",
    )
    Text(
        text = "冷启动或应用在后台时从快捷方式进入，均由 MainActivity 解析 URI 并交给首页导航栈处理。",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
