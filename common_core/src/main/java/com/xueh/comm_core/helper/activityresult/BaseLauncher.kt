package com.xueh.comm_core.helper.activityresult

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * 创 建 人: xueh
 * 创建日期: 2021/7/2 17:13
 * 备注：
 */
abstract class BaseLauncher<I, O>(private val contract: ActivityResultContract<I, O>) :
    DefaultLifecycleObserver,
    ActivityResultCallback<O> {
    lateinit var launcher: ActivityResultLauncher<I>
    lateinit var activity: Activity

    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        if (owner is ComponentActivity) {
            activity = owner
            launcher = owner.registerForActivityResult(contract, this)
        }else if(owner is Fragment){
            activity=owner.activity as ComponentActivity
            launcher = owner.registerForActivityResult(contract, this)
        }
    }

    override fun onActivityResult(result: O?) {

    }
}