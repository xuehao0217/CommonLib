package com.xueh.comm_core.helper.compose

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/29
 * 备注：
 */
//LifecycleEventEffect(Lifecycle.Event.ON_RESUME){

//}

// =========================
// Context 拓展
// =========================
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

// =========================
// NavHostController 拓展
// =========================
fun androidx.navigation.NavHostController.goBackRouteWithParams(
    route: String,
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    getBackStackEntry(route).arguments?.let { callback?.invoke(it) }
    if (autoPop) popBackStack()
}

fun androidx.navigation.NavHostController.goBackWithParams(
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    previousBackStackEntry?.arguments?.let { callback?.invoke(it) }
    if (autoPop) popBackStack()
}

// =========================
// 权限检查 Composable
// =========================
@Composable
fun CheckPermission(permission: String, onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onPermissionGranted()
        }

    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionGranted()
    } else {
        launcher.launch(permission)
    }
}

// =========================
// 屏幕高度 Composable
// =========================
@Composable
fun ScreenHeightInDp() = LocalDensity.current.run {
    LocalContext.current.resources.displayMetrics.heightPixels.toFloat().toDp()
}

// =========================
// Lazy 滚动停止监听（通用）
// =========================
@Composable
private fun <T> onScrollStopVisibleListBase(
    state: T,
    isScrollInProgressProvider: T.() -> Boolean,
    visibleItemsProvider: T.() -> List<Int>,
    delayMs: Long = 80L,
    onStop: (List<Int>) -> Unit,
) {
    val callback by rememberUpdatedState(onStop)
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgressProvider() }
            .distinctUntilChanged()
            .filter { !it }
            .collectLatest {
                delay(delayMs)
                callback(state.visibleItemsProvider())
            }
    }
}

@Composable
fun LazyListState.onScrollStopVisibleList(onStop: (List<Int>) -> Unit) =
    onScrollStopVisibleListBase(
        state = this,
        isScrollInProgressProvider = { isScrollInProgress },
        visibleItemsProvider = { layoutInfo.visibleItemsInfo.map { it.index } },
        onStop = onStop
    )

@Composable
fun LazyGridState.onScrollStopVisibleList(onStop: (List<Int>) -> Unit) =
    onScrollStopVisibleListBase(
        state = this,
        isScrollInProgressProvider = { isScrollInProgress },
        visibleItemsProvider = { layoutInfo.visibleItemsInfo.map { it.index } },
        onStop = onStop
    )

@Composable
fun LazyStaggeredGridState.onScrollStopVisibleList(onStop: (List<Int>) -> Unit) =
    onScrollStopVisibleListBase(
        state = this,
        isScrollInProgressProvider = { isScrollInProgress },
        visibleItemsProvider = { layoutInfo.visibleItemsInfo.map { it.index } },
        onStop = onStop
    )

// =========================
// 滚动方向监听
// =========================
@Composable
fun LazyListState.onScrollDirectionChanged(onDirectionChanged: (Boolean) -> Unit) {
    var lastIndex by remember { mutableStateOf(firstVisibleItemIndex) }
    val callback by rememberUpdatedState(onDirectionChanged)
    LaunchedEffect(this) {
        snapshotFlow { firstVisibleItemIndex }
            .distinctUntilChanged()
            .collectLatest { current ->
                val up = current > lastIndex
                lastIndex = current
                callback(up)
            }
    }
}

// =========================
// LogCompositions - 统计重组次数
// =========================
//@Composable
//fun MyScreen() {
//    var state by remember { mutableStateOf(0) }
//
//    Column {
//        Button(onClick = { state++ }) {
//            Text("点击增加 state")
//        }
//
//        // 打印重组次数
//        LogCompositions("MyScreen")
//    }
//}
@Composable
fun LogCompositions(
    msg: String,
    color: Color = Color.Yellow,
    logTag: String = "RecompositionLog",
) {
    val count = remember { mutableStateOf(0) }
    SideEffect {
        count.value++
        Log.d(logTag, "$msg 重组次数 ${count.value}")
    }
    Text(text = "$msg 重组次数 ${count.value}", color = color)
}

// =========================
// OrderedStateMap - 有序状态 Map
// =========================

//val tabs = remember {
//    orderedStateMapOf(
//        ForYou to viewModel.getLatestNews(ForYou),
//    )
//}

//tabs.addAt(0,"Lotto", viewModel.getLatestNews("Lotto"))

//tabs.put(it.category_name, viewModel.getLatestNews(it.category_id))

class OrderedStateMap<K, V> {
    private val stateMap = mutableStateMapOf<K, V>()
    private val orderList = mutableStateListOf<K>()

    operator fun get(key: K): V? = stateMap[key]
    operator fun set(key: K, value: V) = put(key, value)
    operator fun contains(key: K) = stateMap.containsKey(key)

    fun put(key: K, value: V): V? {
        val previous = stateMap.put(key, value)
        if (key !in orderList) orderList.add(key)
        return previous
    }

    fun addAt(index: Int, key: K, value: V) {
        stateMap[key] = value
        if (key !in orderList) orderList.add(index, key)
    }

    fun remove(key: K): V? {
        orderList.remove(key)
        return stateMap.remove(key)
    }

    fun clear() {
        stateMap.clear()
        orderList.clear()
    }

    val orderedKeys: List<K> get() = orderList.toList()
    val size: Int get() = stateMap.size
    fun isEmpty() = stateMap.isEmpty()
    fun orderedEntries(): List<Pair<K, V>> =
        orderList.mapNotNull { key -> stateMap[key]?.let { value -> key to value } }

    fun putAll(from: Map<out K, V>) = from.forEach { (k, v) -> put(k, v) }
}

fun <K, V> orderedStateMapOf(vararg pairs: Pair<K, V>) =
    OrderedStateMap<K, V>().apply { putAll(pairs.toMap()) }


