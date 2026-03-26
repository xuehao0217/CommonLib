package com.xueh.comm_core.helper.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * 基于 [rememberSaveable] 的页面内 UI 状态小工具。
 *
 * ## 与 Navigation 3 [androidx.navigation3.runtime.NavKey] 的分工
 * - **NavKey / 返回栈**：描述「有哪些屏、顺序与可序列化参数」，由导航运行时持久化（进程被杀时是否恢复取决于实现与配置）。
 * - **rememberSaveable**：保存**当前 Composable 子树内**的轻量 UI 状态（如计数器、展开开关、输入草稿），在**配置变更**
 *   （旋转、深色模式切换等）后仍保留；进程被杀时若启用 saved state 且类型可序列化，也可能恢复。
 *
 * 勿把不可序列化的大对象、Context、ViewModel 放进 Saveable；复杂状态请用 `ViewModel` + `SavedStateHandle`。
 *
 * @param initial 初始整数值；配置变更后恢复时以保存值为准。
 */
@Composable
fun rememberSaveableIntState(initial: Int = 0): MutableState<Int> =
    rememberSaveable { mutableStateOf(initial) }

/**
 * 可保存的单行文本状态（适合演示输入框草稿）。
 */
@Composable
fun rememberSaveableStringState(initial: String = ""): MutableState<String> =
    rememberSaveable { mutableStateOf(initial) }
