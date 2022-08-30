package com.xueh.comm_core.helper.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/29
 * 备注：
 */

//var life = rememberLifecycle()
//life.onLifeResume {
//    viewModel.getMsgList()
//}

class ComposeLifecycleObserver : DefaultLifecycleObserver {
    private var resume: (() -> Unit)? = null
    fun onLifeResume(scope: () -> Unit) {
        resume = scope
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        resume?.invoke()
    }
}

@Composable
fun rememberLifecycle(): ComposeLifecycleObserver {
    val observer = ComposeLifecycleObserver()
    var owner = LocalLifecycleOwner.current
    DisposableEffect(key1 = "lifecycle") {
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }
    val ctx = LocalLifecycleOwner.current
    return remember(ctx) { observer }
}