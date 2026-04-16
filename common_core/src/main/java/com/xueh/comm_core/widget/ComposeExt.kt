/**
 * Modifier 扩展：[clickNoRipple]、[clickDebounce]、[setBackground]（位图背景）、[customRippleClickable]（按压高亮）。
 */
package com.xueh.comm_core.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ImageUtils
import kotlinx.coroutines.flow.collectLatest

/**
 * 无 Material 波纹的点击；语义同「静默点击」。
 * 若需与 [clickable] 区分命名，请优先使用 [clickNoRipple]。
 */
fun Modifier.clickNoRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

/**
 * 防抖点击；[millis] 内重复点击仅触发一次。
 */
fun Modifier.clickDebounce(
    millis: Long = 400,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val currentOnClick by rememberUpdatedState(onClick)

    clickNoRipple {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > millis) {
            currentOnClick()
            lastClickTime = now
        }
    }
}

/**
 * 将 drawable 解码为 [androidx.compose.ui.graphics.ImageBitmap] 后绘制在背景上。
 *
 * 适合小图标/纹理；大图更占内存，复杂场景请改用 [androidx.compose.ui.graphics.painter.Painter] / Coil。
 */
fun Modifier.setBackground(@DrawableRes backIcon: Int): Modifier = composed {
    val bitmap = remember(backIcon) {
        ImageUtils.getBitmap(backIcon).asImageBitmap()
    }
    drawBehind {
        drawImage(bitmap)
    }
}

/**
 * 按下时显示圆角底色高亮（无默认波纹）。
 *
 * @param cornerRadiusDp 圆角（dp）
 */
fun Modifier.customRippleClickable(
    rippleColor: Color = Color.Blue.copy(alpha = 0.3f),
    cornerRadiusDp: Float = 8f,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }

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
            shape = RoundedCornerShape(cornerRadiusDp.dp),
        )
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
}
