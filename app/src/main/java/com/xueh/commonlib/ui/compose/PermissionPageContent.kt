package com.xueh.commonlib.ui.compose

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ToastUtils
/**
 * 权限申请演示：使用 [ActivityResultContracts]（官方推荐），不再依赖已停止维护的 Accompanist Permissions。
 */
@Preview
@Composable
fun PermissionPageContent() {
    var singlePermissionStr by remember { mutableStateOf("申请单个权限") }
    var multiplePermissionStr by remember { mutableStateOf("申请多个权限") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            singlePermissionStr = "申请单个权限-已授权"
            ToastUtils.showShort("已授权")
        } else {
            singlePermissionStr = "申请单个权限-已拒绝"
            ToastUtils.showShort("已拒绝相机权限")
        }
    }

    val multipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            multiplePermissionStr = "申请多个权限-全部允许"
            ToastUtils.showShort("用户全部允许")
        } else {
            multiplePermissionStr = "申请多个权限-未全部允许"
            ToastUtils.showShort("部分或全部权限未授予")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DemoScreenIntro(
            text = "使用 ActivityResultContracts 申请权限；结果通过 Toast 与按钮文案反馈。",
        )
        PermissionTonalButton(
            label = singlePermissionStr,
            onClick = { cameraLauncher.launch(Manifest.permission.CAMERA) },
        )
        PermissionTonalButton(
            label = multiplePermissionStr,
            onClick = {
                multipleLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ),
                )
            },
        )
    }
}

@Composable
private fun PermissionTonalButton(
    label: String,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
