package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/13
 * 备注：
 */
//去除水波纹
@Composable
fun Modifier.click(click: () -> Unit) = clickable(
    indication = null,
    interactionSource = remember {
        MutableInteractionSource()
    }) {
    click.invoke()
}

//防抖
@Composable
fun Modifier.clickDefendFast(millis: Long = 800, onClick: () -> Unit): Modifier {
    var timeStamp by remember {
        mutableStateOf(0L)
    }

    return click {
        if (System.currentTimeMillis() - timeStamp > millis) {
            onClick()
            timeStamp = System.currentTimeMillis()
        }
    }
}