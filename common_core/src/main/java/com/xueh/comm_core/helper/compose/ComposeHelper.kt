package com.xueh.comm_core.helper.compose

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.lt.compose_views.util.rememberMutableStateOf

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/29
 * 备注：
 */

//LifecycleEventEffect(Lifecycle.Event.ON_RESUME){

//}


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




@Composable
fun CheckPermission( permission:String,onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted.invoke()
            }
        }
    // 检查权限是否已授权
    val hasPermission =
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
        onPermissionGranted.invoke()
    } else {
        // 请求权限
        requestPermissionLauncher.launch(permission)
    }
}


@Composable
fun ScreenHeightInDp()= LocalDensity.current.run { LocalContext.current.resources.displayMetrics.heightPixels.toFloat().toDp() }



//LazyListState 滑动停止 可见条目
@Composable
fun LazyListState.onScrollStopVisibleList(scrollStop: (visibleItem: List<Int>) -> Unit){
    LaunchedEffect(this) {
        snapshotFlow { isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                // 滑动停止
                val visibleItemsIndex =
                    layoutInfo.visibleItemsInfo.map { it.index }.toList()
                scrollStop.invoke(visibleItemsIndex)
            }
        }
    }
}

@Composable
fun LazyGridState.onScrollStopVisibleList(scrollStop: (visibleItem: List<Int>) -> Unit){
    LaunchedEffect(this) {
        snapshotFlow { isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                // 滑动停止
                val visibleItemsIndex =
                    layoutInfo.visibleItemsInfo.map { it.index }.toList()
                scrollStop.invoke(visibleItemsIndex)
            }
        }
    }
}
@Composable
fun LazyStaggeredGridState.onScrollStopVisibleList(scrollStop: (visibleItem: List<Int>) -> Unit){
    LaunchedEffect(this) {
        snapshotFlow { isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                // 滑动停止
                val visibleItemsIndex =
                    layoutInfo.visibleItemsInfo.map { it.index }.toList()
                scrollStop.invoke(visibleItemsIndex)
            }
        }
    }
}


//LazyListState 滑动方向
@Composable
fun LazyListState.onScrollDirection(scrollDirection: (isScrollingUp: Boolean) -> Unit){
    var lastFirstIndex by rememberMutableStateOf { 0 }
    var isScrollingUp :Boolean
    LaunchedEffect(this) {
        snapshotFlow { isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                isScrollingUp = if (firstVisibleItemIndex > lastFirstIndex) {
                    // 上滑
                    true
                } else {
                    //下滑
                    false
                }
                lastFirstIndex = firstVisibleItemIndex
                scrollDirection.invoke( isScrollingUp)
            }
        }
    }
}