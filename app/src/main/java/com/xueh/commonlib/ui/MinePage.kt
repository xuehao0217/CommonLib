package com.xueh.commonlib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.theme.AppBaseTheme
import com.xueh.comm_core.base.compose.theme.AppTheme
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.components.click
import com.xueh.commonlib.ui.compose.DemoListRow

/**
 * 「我的」页：调试入口（Tab 拦截、主题、AgentWeb 等）。
 */
@Composable
fun MinePage() {
    val shell = LocalAppShell.current
    Column(
        Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 0.dp, vertical = 8.dp)
    ) {
        Text(
            text = "调试与外链；完整演示请从「首页」进入。",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
        )
        SectionLabel("外链")
        DemoListRow(title = "AgentWeb + Compose 标题栏（透明标题/分享）") {
            shell.openAgentWeb(
                "https://www.bilibili.com?hideTitle=1&showShare=1",
                "示例页",
            )
        }
        SectionLabel("调试")
        DemoListRow(title = "是否拦截第三个 Tab：${MainActivity.interceptTab}") {
            MainActivity.interceptTab = !MainActivity.interceptTab
        }
        DemoListRow(title = "「我的」Tab 小红点：${MainActivity.showRedPoint.value}") {
            MainActivity.showRedPoint.value = !MainActivity.showRedPoint.value
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 4.dp),
    )
}

/** 第三个 Tab：主题切换与全屏测试页入口 */
@Composable
fun TabPage3() {
    val shell = LocalAppShell.current
    Column(
        Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = "主题与测试页",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        DemoListRow(title = "切换为深色 Theme") {
            appThemeType = AppThemeType.Dark
        }
        DemoListRow(title = "切换为浅色 Theme") {
            appThemeType = AppThemeType.Light
        }
        DemoListRow(title = "跟随系统 Theme") {
            appThemeType = AppThemeType.FOLLOW_SYSTEM
        }

        Text(
            text = "点击下方色块打开全屏测试页（单 Activity 内覆盖层）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 8.dp),
        )
        AppBaseTheme {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(AppTheme.colors.theme)
                    .click {
                        shell.openTestComposeDemo()
                    }
            )
        }
    }
}
