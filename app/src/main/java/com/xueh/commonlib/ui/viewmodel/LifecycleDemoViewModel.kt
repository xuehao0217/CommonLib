package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 演示用 ViewModel（无 Hilt）：对外暴露 [tickSec]，供界面通过
 * `collectAsStateWithLifecycleWhileStarted`（见 common_core `ComposeFlowLifecycle`）订阅。
 *
 * 进入后台后收集会暂停，秒数停止递增；回前台后继续。
 */
class LifecycleDemoViewModel : ViewModel() {

    private val _tickSec = MutableStateFlow(0)
    val tickSec: StateFlow<Int> = _tickSec.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                _tickSec.value = _tickSec.value + 1
            }
        }
    }
}
