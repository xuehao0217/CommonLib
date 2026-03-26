package com.xueh.commonlib.ui.compose

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.widget.SpacerH

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

    Column {
        ItemView(singlePermissionStr) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
        SpacerH(10)
        ItemView(multiplePermissionStr) {
            multipleLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
            )
        }
    }
}
