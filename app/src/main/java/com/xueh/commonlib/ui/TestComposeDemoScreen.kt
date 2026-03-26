package com.xueh.commonlib.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.helper.compose.findActivity
import com.xueh.comm_core.components.click

/**
 * 原 TestComposeActivity 的演示 UI，现作为单 Activity 内的全屏层（返回键由 [MainActivity] 统一关闭覆盖层）。
 */
@Composable
fun TestComposeDemoScreen() {
    val shell = LocalAppShell.current
    BackHandler { shell.dismissOverlay() }
    val activity = LocalContext.current.findActivity() as? BaseComposeActivity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1565C0))
            .click {
                activity?.let { it.isSystemBarLight = !it.isSystemBarLight }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "TestCompose 演示\n点击切换状态栏图标颜色\n再按系统返回关闭",
            color = Color.White,
        )
    }
}
