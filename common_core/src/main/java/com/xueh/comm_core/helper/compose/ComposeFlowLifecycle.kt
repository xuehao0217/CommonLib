package com.xueh.comm_core.helper.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Flow 在 Compose 中的生命周期感知收集封装。
 *
 * ## 与 `LaunchedEffect { flow.collect { } }` 的区别
 * - `LaunchedEffect` 内直接 `collect`：只要 Composable 仍在组合中就会持续收集，**与 Activity 是否在前台无关**，
 *   可能造成 STOPPED 后仍更新 UI 或浪费资源。
 * - [collectAsStateWithLifecycleWhileStarted]：仅在生命周期至少为 [Lifecycle.State.STARTED] 时收集；
 *   进入后台（ON_STOP）后取消上游收集，回到前台再恢复。
 *
 * ## 适用场景
 * - 将 ViewModel 暴露的 `StateFlow` / `Flow` 转为 Compose [State] 驱动界面。
 * - 展示用数据流；一次性副作用（如导航）仍宜用 `LaunchedEffect` + `flow.first()` 等组合。
 *
 * @param initialValue 收集到首条值之前的占位；会作为 [State] 的初始值。
 * @param minActiveState 默认 [Lifecycle.State.STARTED]；可按需改为 CREATED 等（少见）。
 */
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleWhileStarted(
    initialValue: T,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): State<T> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return collectAsStateWithLifecycle(
        initialValue = initialValue,
        lifecycle = lifecycle,
        minActiveState = minActiveState,
    )
}
