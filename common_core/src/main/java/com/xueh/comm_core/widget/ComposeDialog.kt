/**
 * 弹窗相关组件：[BottomSheetDialog]、[CustomDialog]、[BaseAnyPopDialog]、[BaseComposeDialog]、[ComposeLoadingDialog]。
 *
 * **ComposeLoadingDialog 流程**：ViewModel 将 [androidx.compose.runtime.MutableState] 置 true → 全屏 [Dialog] + [CircularProgressIndicator]；
 * 请求结束在 [com.xueh.comm_core.net.coroutinedsl.RequestViewModel.apiFinally] 等处置 false（参见 [com.xueh.comm_core.base.mvvm.BaseComposeViewModel]）。
 */
package com.xueh.comm_core.widget

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
import androidx.compose.runtime.mutableFloatStateOf
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

@Composable
private fun DialogOverlay(
    visible: Boolean,
    backgroundColor: Color = Color(0x99000000),
    onClick: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = LinearEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = LinearEasing)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .then(if (onClick != null) Modifier.clickNoRipple { onClick() } else Modifier),
        )
    }
}

@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    BackHandler(enabled = visible, onBack = {
        if (cancelable) {
            onDismissRequest()
        }
    })
    Box(modifier = modifier) {
        DialogOverlay(
            visible = visible,
            onClick = if (canceledOnTouchOutside) onDismissRequest else null,
        )
        InnerDialog(
            visible = visible,
            cancelable = cancelable,
            onDismissRequest = onDismissRequest,
            content = content,
        )
    }
}

@Composable
private fun BoxScope.InnerDialog(
    visible: Boolean,
    cancelable: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    var offsetY by remember {
        mutableFloatStateOf(0f)
    }
    val offsetYAnimate by animateFloatAsState(targetValue = offsetY)
    var bottomSheetHeight by remember { mutableFloatStateOf(0f) }
    AnimatedVisibility(
        modifier = Modifier
            .clickNoRipple { }
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
                    },
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
                },
            ),
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            initialOffsetY = { 2 * it },
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            targetOffsetY = { it },
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

@Composable
fun CustomDialog(
    contentAlignment: Alignment = Alignment.TopStart,
    dialogBackgroundColor: Color = Color(0x99000000),
    modifier: Modifier = Modifier,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    BackHandler(enabled = visible, onBack = {
        if (cancelable) {
            onDismissRequest()
        }
    })
    Box(modifier = modifier) {
        DialogOverlay(
            visible = visible,
            backgroundColor = dialogBackgroundColor,
        )
        var offsetY by remember {
            mutableFloatStateOf(0f)
        }
        val offsetYAnimate by animateFloatAsState(targetValue = offsetY)
        var bottomSheetHeight by remember { mutableFloatStateOf(0f) }
        AnimatedVisibility(
            modifier = Modifier
                .clickNoRipple { }
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
                initialOffsetY = { 2 * it },
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                targetOffsetY = { it },
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
                    .clickNoRipple {
                        if (canceledOnTouchOutside) {
                            onDismissRequest()
                        }
                    },
            ) {
                content()
            }
        }
    }
}

@Composable
fun BaseAnyPopDialog(
    showDialog: MutableState<Boolean>,
    direction: DirectionState,
    content: @Composable () -> Unit,
) {
    if (showDialog.value) {
        val isActiveClose by remember { mutableStateOf(false) }
        AnyPopDialog(
            modifier = Modifier.fillMaxWidth(),
            isActiveClose = isActiveClose,
            properties = AnyPopDialogProperties(direction = direction),
            content = {
                content()
            },
            onDismiss = {
                showDialog.value = false
            },
        )
    }
}

@Composable
fun BaseComposeDialog(
    alertDialog: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false,
    ),
    onDismiss: () -> Unit = {},
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

@Composable
fun ComposeLoadingDialog(
    alertDialog: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false,
    ),
    onDismiss: () -> Unit = {},
) {
    if (alertDialog.value) {
        BaseComposeDialog(
            alertDialog,
            properties = properties,
            onDismiss = onDismiss,
        ) {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
    }
}
