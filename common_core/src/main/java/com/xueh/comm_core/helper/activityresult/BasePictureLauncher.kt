package com.xueh.comm_core.helper.activityresult

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.xueh.comm_core.helper.activityresult.permission.MultiPermissionLauncher
import com.xueh.comm_core.helper.activityresult.permission.PermissionLauncher


abstract class BasePictureLauncher<I, O>(contract: ActivityResultContract<I, O>) :
    BaseLauncher<I, O>(contract) {
    val permission by lazy { MultiPermissionLauncher() }
    val cropImage by lazy { CropImageLauncher() }

    override fun onCreate(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(permission)
        owner.lifecycle.addObserver(cropImage)
        super.onCreate(owner)
    }
}