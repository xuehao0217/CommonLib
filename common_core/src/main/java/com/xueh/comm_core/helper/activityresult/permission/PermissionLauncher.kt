package com.xueh.comm_core.helper.activityresult.permission

import androidx.activity.result.contract.ActivityResultContracts
import com.xueh.comm_core.helper.activityresult.BaseLauncher


class PermissionLauncher :
    BaseLauncher<String, Boolean>(ActivityResultContracts.RequestPermission()) {
    var granted: (permission: String) -> Unit = {}
    var denied: (permission: String) -> Unit = {}
    var explained: (permission: String) -> Unit = {}
    var permission: String = ""

    fun lunch(
        permission: String,
        granted: (permission: String) -> Unit = {},
        explained: (permission: String) -> Unit = {},
        denied: (permission: String) -> Unit = {}
    ) {
        this.granted = granted
        this.explained = explained
        this.denied = denied
        this.permission = permission
        launcher.launch(permission)
    }

    override fun onActivityResult(result: Boolean?) {
        when {
            result == true -> granted.invoke(permission)
            else -> explained.invoke(permission)
        }
    }
}