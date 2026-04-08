package com.xueh.commonlib.ui.compose.playground

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/** 共享元素转场在不同版本 API 略有差异；此处用 scale + 透明度模拟连续观感；正式项目请查官方 SharedTransitionLayout。 */
@Composable
fun PlaygroundSharedTransitionSection() {
    PlaygroundSectionCaption(
        "降级示意：animateFloatAsState + graphicsLayer 缩放。接入 SharedTransitionLayout 时请对齐当前 androidx.compose.animation 文档中的 rememberSharedContentState / sharedElement。",
    )
    var expanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.92f,
        animationSpec = tween(320),
        label = "hero-scale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.95f,
        animationSpec = tween(320),
        label = "hero-alpha",
    )
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(if (expanded) 120.dp else 88.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { expanded = !expanded },
        color = if (expanded) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        shape = MaterialTheme.shapes.large,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                if (expanded) {
                    "详情态 · 再点收回（scale 模拟共享轴）"
                } else {
                    "缩略态 · 点击放大"
                },
                style = MaterialTheme.typography.titleSmall,
                color = if (expanded) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        }
    }
}
