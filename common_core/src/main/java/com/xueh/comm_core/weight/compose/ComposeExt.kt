package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ImageUtils
import kotlinx.coroutines.flow.collectLatest

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/26
 * 备注：Compose 点击扩展 & 防抖 & 背景绘制优化
 */

// ---------------------------
// 无水波纹点击
// ---------------------------
fun Modifier.click(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

// ---------------------------
// 防抖点击
// ---------------------------
fun Modifier.clickDebounce(
    millis: Long = 800,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableStateOf(0L) }
    val currentOnClick by rememberUpdatedState(onClick)

    click {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > millis) {
            currentOnClick()
            lastClickTime = now
        }
    }
}

// ---------------------------
// 背景图片
// ---------------------------
fun Modifier.setBackground(@DrawableRes backIcon: Int): Modifier = composed {
    // 缓存 Bitmap 避免每次重新加载
    val bitmap = remember(backIcon) {
        ImageUtils.getBitmap(backIcon).asImageBitmap()
    }
    drawBehind {
        drawImage(bitmap)
    }
}


// ---------------------------
// 自定义点击高亮扩展，不依赖已弃用 IndicationInstance
// ---------------------------
fun Modifier.customRippleClickable(
    rippleColor: Color = Color.Blue.copy(alpha = 0.3f),
    cornerRadius: Float = 8f,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }

    // 监听按下状态
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            pressed = when (interaction) {
                is PressInteraction.Press -> true
                is PressInteraction.Release, is PressInteraction.Cancel -> false
                else -> false
            }
        }
    }

    this
        .background(
            color = if (pressed) rippleColor else Color.Transparent,
            shape = RoundedCornerShape(cornerRadius.dp)
        )
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}
