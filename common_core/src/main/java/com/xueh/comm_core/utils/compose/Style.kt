package com.xueh.comm_core.utils.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/25
 * 备注：
 */

val MyLocalIndication = staticCompositionLocalOf<Indication> {
    DefaultDebugIndication
}

object DefaultDebugIndication : Indication {

    private class DefaultDebugIndicationInstance(
        private val isPressed: State<Boolean>,
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value) {
                drawRoundRect(color = Color.Black.copy(alpha = 0.3f), size = size, cornerRadius = CornerRadius(5f))
            } else if (isHovered.value || isFocused.value) {
                drawRect(color = Color.Black.copy(alpha = 0.1f), size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            DefaultDebugIndicationInstance(isPressed, isHovered, isFocused)
        }
    }
}

@Composable
fun Modifier.clickState(click: () -> Unit) = clickable(
    indication = MyLocalIndication.current,
    interactionSource = remember {
        MutableInteractionSource()
    }) {
    click.invoke()
}

@Composable
fun Modifier.click(click: () -> Unit)=clickable(
    indication = null,
    interactionSource = remember {
        MutableInteractionSource()
    }){
    click.invoke()
}