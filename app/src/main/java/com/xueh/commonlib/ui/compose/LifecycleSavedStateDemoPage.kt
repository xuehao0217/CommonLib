package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xueh.comm_core.helper.compose.collectAsStateWithLifecycleWhileStarted
import com.xueh.comm_core.helper.compose.rememberSaveableIntState
import com.xueh.comm_core.helper.compose.rememberSaveableStringState
import com.xueh.commonlib.ui.viewmodel.LifecycleDemoViewModel

/**
 * Navigation 3 演示页：生命周期感知 Flow 收集 + Saveable 状态。
 *
 * - **区块 A**：配置变更（如旋转）后仍保留计数与输入草稿。
 * - **区块 B**：[LifecycleDemoViewModel.tickSec] 仅在界面 STARTED 时递增展示。
 * - **区块 C**：说明 [com.xueh.comm_core.base.mvvm.BaseComposeViewModel] 与请求异常收集的关系。
 */
@Composable
fun LifecycleSavedStateDemoPage() {
    val scroll = rememberScrollState()
    val vm: LifecycleDemoViewModel = viewModel()
    val tickSec by vm.tickSec.collectAsStateWithLifecycleWhileStarted(initialValue = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "生命周期与 Saveable",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "旋转屏幕或切换深色模式后，下方 A 区计数与文字应保留；B 区秒表在后台时暂停。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ----- A：rememberSaveable（common_core 封装） -----
        Text("A. rememberSaveable（配置变更后保留）", style = MaterialTheme.typography.titleMedium)
        var saveCount by rememberSaveableIntState(0)
        var draft by rememberSaveableStringState("")
        Text("计数: $saveCount", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { saveCount++ }) { Text("计数 +1") }
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it },
            label = { Text("可保存草稿") },
            singleLine = true,
            modifier = Modifier.padding(top = 4.dp),
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ----- B：collectAsStateWithLifecycleWhileStarted -----
        Text("B. Flow + 生命周期收集", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "ViewModel 每秒 +1；仅当本页处于 STARTED 时 UI 才订阅（见 LifecycleDemoViewModel）。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text("已运行秒数（界面可见时递增）: $tickSec", style = MaterialTheme.typography.bodyLarge)

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ----- C：BaseComposeViewModel -----
        Text("C. 网络请求与 BaseComposeViewModel", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "发起接口的页面请使用 com.xueh.comm_core.base.mvvm.BaseComposeViewModel：\n" +
                "• Loading 由 apiLoadingState 驱动 ComposeLoadingDialog。\n" +
                "• 异常在 Lifecycle STARTED 下从 apiExceptionState 收集，进入后台后不再消费，避免无效回调。\n" +
                "示例入口：本演示列表中的「ComposeViewModel」。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
