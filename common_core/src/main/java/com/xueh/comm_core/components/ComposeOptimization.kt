package com.xueh.comm_core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

/**
 * 创 建 人: xueh
 * 创建日期: 2022/9/13
 *
 * Compose 编译器已做大量重组范围优化；[BoxWrapper] 仅适用于需要将昂贵子树与父级频繁变化的状态隔离时的显式边界。
 */
@Composable
fun BoxWrapper(content: @Composable () -> Unit) {
    Box {
        content()
    }
}
