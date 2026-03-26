package com.xueh.commonlib.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 单 Activity 内全屏覆盖层（AgentWeb、测试页等）。
 */
sealed class FullScreenOverlay {
    data object None : FullScreenOverlay()
    data object TestComposeDemo : FullScreenOverlay()
    data class AgentWeb(val url: String, val title: String = "") : FullScreenOverlay()
}

/**
 * 由 [MainActivity] 提供，子页面通过 [LocalAppShell] 打开全屏层。
 */
class AppShellController(
    private val overlayState: MutableState<FullScreenOverlay>,
) {
    fun openAgentWeb(url: String, title: String = "") {
        overlayState.value = FullScreenOverlay.AgentWeb(url, title)
    }

    fun openTestComposeDemo() {
        overlayState.value = FullScreenOverlay.TestComposeDemo
    }

    fun dismissOverlay() {
        overlayState.value = FullScreenOverlay.None
    }
}

val LocalAppShell = staticCompositionLocalOf<AppShellController> {
    error("LocalAppShell 未提供，应在 MainActivity 根节点 CompositionLocalProvider 中注入")
}
