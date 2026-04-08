package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PlaygroundSnackbarDemo(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
) {
    PlaygroundSectionTitle("Snackbar（Scaffold 已挂 SnackbarHost）")
    PlaygroundSectionCaption("需remember SnackbarHostState；带 action 时可返回 SnackbarResult.ActionPerformed。")
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Button(
            onClick = {
                scope.launch {
                    val r = snackbarHostState.showSnackbar(
                        message = "已执行操作（演示）",
                        actionLabel = "撤销",
                        withDismissAction = true,
                    )
                    if (r == SnackbarResult.ActionPerformed) {
                        snackbarHostState.showSnackbar("已撤销")
                    }
                }
            },
        ) {
            Text("Snackbar（消息 + 操作 + 关闭）")
        }
        FilledTonalButton(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("仅文本，无按钮")
                }
            },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text("仅消息 Snackbar")
        }
    }
}
