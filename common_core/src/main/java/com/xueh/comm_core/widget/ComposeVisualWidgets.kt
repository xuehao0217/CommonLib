/** 视觉类小组件：旋转加载动画、渐变条等装饰性 Composable。 */
package com.xueh.comm_core.widget

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp

@Composable
fun AnimLoading(@DrawableRes id: Int, size: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "anim_loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )
    val imageBitmap = ImageBitmap.imageResource(id = id)
    Canvas(Modifier.size(size.dp)) {
        rotate(rotation) { drawImage(imageBitmap) }
    }
}

@Composable
fun ShadowVerticalView(colors: List<Color>, height: Int = 50, modifier: Modifier = Modifier) {
    val brush = Brush.verticalGradient(colors)
    Box(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(height.dp).background(brush))
    }
}
