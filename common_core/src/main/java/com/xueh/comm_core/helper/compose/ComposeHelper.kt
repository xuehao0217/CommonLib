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









class OrderedStateMap<K, V> {
    private val stateMap = mutableStateMapOf<K, V>()
    private val orderList = mutableStateListOf<K>()

    operator fun get(key: K): V? = stateMap[key]

    // 获取所有key（按添加顺序）
    val orderedKeys: List<K> get() = orderList.toList()
    operator fun set(key: K, value: V) {
        stateMap[key] = value
        if (!orderList.contains(key)) {
            orderList.add(key)
        }
    }

    fun put(key: K, value: V): V? {
        val previous = stateMap.put(key, value)
        if (!orderList.contains(key)) {
            orderList.add(key)
        }
        return previous
    }

    fun remove(key: K): V? {
        orderList.remove(key)
        return stateMap.remove(key)
    }

    fun clear() {
        orderList.clear()
        stateMap.clear()
    }

    // 获取有序的条目列表
    fun orderedEntries(): List<Map.Entry<K, V>> {
        return orderList.mapNotNull { key ->
            stateMap[key]?.let { value ->
                object : Map.Entry<K, V> {
                    override val key: K = key
                    override val value: V = value
                }
            }
        }
    }

    // 其他便利方法
    val size: Int get() = stateMap.size
    fun isEmpty(): Boolean = stateMap.isEmpty()
    fun containsKey(key: K): Boolean = stateMap.containsKey(key)
    fun containsValue(value: V): Boolean = stateMap.containsValue(value)

    fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }
}

// 创建函数
fun <K, V> orderedStateMapOf(vararg pairs: Pair<K, V>): OrderedStateMap<K, V> {
    return OrderedStateMap<K, V>().apply {
        putAll(pairs.toMap())
    }
}