package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.blankj.utilcode.util.ToastUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.weight.compose.SpacerH

/**
 * 创 建 人: xueh
 * 创建日期: 2023/5/25
 * 备注：
 */
@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PermissionPageContent() {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val multiplePermissionState = rememberMultiplePermissionsState(
        listOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    )

    var singlePermissionStr by rememberMutableStateOf{  "申请单个权限"}
    var multiplePermissionStr by rememberMutableStateOf{"申请多个权限"}

    when (cameraPermissionState.status) {
        PermissionStatus.Granted -> {//已授权
            singlePermissionStr="申请单个权限-已授权"
            ToastUtils.showShort("已授权")
        }

        is PermissionStatus.Denied -> {
            if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                //如果用户拒绝了该权限但可以显示理由，那么请温和地解释为什么应用程序需要此权限(拒绝权限)
//                "The camera is important for this app. Please grant the permission."
                ToastUtils.showShort("拒绝权限")
                singlePermissionStr="申请单个权限-拒绝"
            } else {
                ToastUtils.showShort("用户选择拒绝且不再询问")
                singlePermissionStr="申请单个权限-拒绝且不再询问"
                //如果这是用户第一次登陆此功能，或者用户不想再次被要求获得此权限，请说明该权限是必需的(用户选择拒绝且不再询问)
//                "Camera permission required for this feature to be available. " +
//                        "Please grant the permission"
            }
        }
    }

    if (multiplePermissionState.allPermissionsGranted) {
        ToastUtils.showShort("用户全部允许")
        multiplePermissionStr="申请多个权限-全部允许"
    }

    Column {
        itemView(singlePermissionStr, showimg = false) {
            cameraPermissionState.launchPermissionRequest()
        }
        SpacerH(int = 10)
        itemView(multiplePermissionStr, showimg = false) {
            multiplePermissionState.launchMultiplePermissionRequest()
        }
    }

}