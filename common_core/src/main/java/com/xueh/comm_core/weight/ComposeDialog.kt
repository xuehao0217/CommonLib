package com.xueh.comm_core.weight

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.melody.dialog.any_pop.AnyPopDialog
import com.melody.dialog.any_pop.AnyPopDialogProperties
import com.melody.dialog.any_pop.DirectionState
import kotlin.math.roundToInt

/**
 * 通用弹窗遮罩层，带渐入渐出动画。
 *
 * @param visible 是否显示遮罩
 * @param backgroundColor 遮罩背景色
 * @param onClick 点击遮罩回调
 */
@Composable
private fun DialogOverlay(
    visible: Boolean,
    backgroundColor: Color = Color(0x99000000),
    onClick: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = LinearEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = LinearEasing))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .then(if (onClick != null) Modifier.click { onClick() } else Modifier)
        )
    }
}

/**
 * 自定义底部抽屉弹窗，支持拖拽下滑关闭。
 *
 * 创 建 人: xueh
 * 创建日期: 2022/11/3
 * 备注： https://juejin.cn/post/7151792921698631717
 */
@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    BackHandler(enabled = visible, onBack = {
        if (cancelable) {
            onDismissRequest()
        }
    })
    Box(modifier = modifier) {
        DialogOverlay(
            visible = visible,
            onClick = if (canceledOnTouchOutside) onDismissRequest else null
        )
        InnerDialog(
            visible = visible,
            cancelable = cancelable,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}

@Composable
private fun BoxScope.InnerDialog(
    visible: Boolean,
    cancelable: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetY by remember {
        mutableStateOf(0f)
    }
    val offsetYAnimate by animateFloatAsState(targetValue = offsetY)
    var bottomSheetHeight by remember { mutableStateOf(0f) }
    AnimatedVisibility(
        modifier = Modifier
            .click { }
            .align(alignment = Alignment.BottomCenter)
            .onGloballyPositioned {
                bottomSheetHeight = it.size.height.toFloat()
            }
            .offset(offset = {
                IntOffset(0, offsetYAnimate.roundToInt())
            })
            .draggable(
                state = rememberDraggableState(
                    onDelta = {
                        offsetY = (offsetY + it.toInt()).coerceAtLeast(0f)
                    }
                ),
                orientation = Orientation.Vertical,
                onDragStarted = {

                },
                onDragStopped = {
                    if (cancelable && offsetY > bottomSheetHeight / 2) {
                        onDismissRequest()
                    } else {
                        offsetY = 0f
                    }
                }
            ),
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            initialOffsetY = { 2 * it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            targetOffsetY = { it }
        ),
    ) {
        DisposableEffect(key1 = null) {
            onDispose {
                offsetY = 0f
            }
        }
        content()
    }
}


/**
 * 全屏遮罩弹窗，内容区域可自定义对齐方式。
 */
@Composable
fun CustomDialog(
    contentAlignment: Alignment = Alignment.TopStart,
    dialogBackgroundColor: Color = Color(0x99000000),
    modifier: Modifier = Modifier,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    BackHandler(enabled = visible, onBack = {
        if (cancelable) {
            onDismissRequest()
        }
    })
    Box(modifier = modifier) {
        DialogOverlay(
            visible = visible,
            backgroundColor = dialogBackgroundColor
        )
        var offsetY by remember {
            mutableStateOf(0f)
        }
        val offsetYAnimate by animateFloatAsState(targetValue = offsetY)
        var bottomSheetHeight by remember { mutableStateOf(0f) }
        AnimatedVisibility(
            modifier = Modifier
                .click { }
                .align(alignment = Alignment.BottomCenter)
                .onGloballyPositioned {
                    bottomSheetHeight = it.size.height.toFloat()
                }
                .offset(offset = {
                    IntOffset(0, offsetYAnimate.roundToInt())
                }),
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                initialOffsetY = { 2 * it }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                targetOffsetY = { it }
            ),
        ) {
            DisposableEffect(key1 = null) {
                onDispose {
                    offsetY = 0f
                }
            }
            Box(
                contentAlignment = contentAlignment,
                modifier = Modifier
                    .fillMaxSize()
                    .click {
                        if (canceledOnTouchOutside) {
                            onDismissRequest()
                        }
                    }) {
                content()
            }

        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * 基于 AnyPopDialog 的通用弹窗封装，支持指定弹出方向。
 */
@Composable
fun BaseAnyPopDialog(
    showDialog: MutableState<Boolean>,
    direction: DirectionState,
    content: @Composable () -> Unit,
) {
    if (showDialog.value) {
        val isActiveClose by remember { mutableStateOf(false) }
        AnyPopDialog(
            modifier = Modifier.fillMaxWidth(), isActiveClose = isActiveClose,
            properties = AnyPopDialogProperties(direction = direction),
            content = {
                content()
            },
            onDismiss = {
                showDialog.value = false
            }
        )
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * 基于系统 Dialog 的 Compose 弹窗封装。
 */
@Composable
fun BaseComposeDialog(
    alertDialog: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismiss:()->Unit={},
    content: @Composable () -> Unit,
) {
    if (alertDialog.value) {
        Dialog(
            onDismissRequest = {
                onDismiss()
                alertDialog.value = false
            },
            properties = properties,
        ) {
            content()
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * 带 CircularProgressIndicator 的加载中弹窗。
 */
@Composable
fun ComposeLoadingDialog(
    alertDialog: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismiss:()->Unit={},
) {
    if (alertDialog.value) {
        BaseComposeDialog(
            alertDialog,
            properties = properties,
            onDismiss=onDismiss,
        ) {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
