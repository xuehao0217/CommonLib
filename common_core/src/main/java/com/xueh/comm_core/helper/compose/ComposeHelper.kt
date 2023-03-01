package com.xueh.comm_core.helper.compose

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/29
 * 备注：
 */


/**
 * 快捷使用remember { mutableStateOf(T) }
 * Quick use remember { mutableStateOf(T) }
 */
@Composable
fun <T> rememberMutableStateOf(value: T): MutableState<T> = remember { mutableStateOf(value) }


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



/**
 * 返回指定的route并回调参数
 */
fun NavHostController.goBackRouteWithParams(
    route: String,
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    getBackStackEntry(route).arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

/**
 * 回到上级页面，并回调参数
 */
fun NavHostController.goBackWithParams(
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    previousBackStackEntry?.arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}