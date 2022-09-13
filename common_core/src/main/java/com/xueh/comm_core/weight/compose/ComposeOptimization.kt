package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

/**
 * 创 建 人: xueh
 * 创建日期: 2022/9/13
 * 备注：缩小重组范围提高性能
 */
@Composable
fun BoxWrapper(content: @Composable () -> Unit) {
    Box {
        content()
    }
}
