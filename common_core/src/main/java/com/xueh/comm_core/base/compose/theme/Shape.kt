/**
 * Material3 形状集：导出顶层 `Shapes`（小/中/大圆角），可在主题中引用以统一卡片、按钮圆角。
 */
package com.xueh.comm_core.base.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)