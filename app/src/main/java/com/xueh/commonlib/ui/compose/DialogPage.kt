package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.xueh.comm_core.widget.BottomSheetDialog
import com.xueh.comm_core.widget.CustomDialog

/**
 * 各类 Dialog / BottomSheet 演示（Material 3）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DialogPage() {
    val alertDialog = remember { mutableStateOf(false) }
    val fullScreenDialog = remember { mutableStateOf(false) }
    var bottomSheetDialog by remember { mutableStateOf(false) }
    var customDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoScreenIntro(
            text = "点击按钮打开对应弹层；Modal BottomSheet 使用 Material3 组件。",
        )
        DialogLaunchButton(text = "Alert Dialog") { alertDialog.value = true }
        DialogLaunchButton(text = "FullScreenDialog") { fullScreenDialog.value = true }
        DialogLaunchButton(text = "BottomSheetDialog（业务封装）") { bottomSheetDialog = true }
        DialogLaunchButton(text = "CustomDialog") { customDialog = true }
        DialogLaunchButton(text = "ModalBottomSheet（M3）") { showBottomSheet = true }
    }

    MyDialog(alertDialog) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
        }
    }

    FullScreenDialog(fullScreenDialog) {
        ConstraintLayout(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
        }
    }

    BottomSheetDialog(
        modifier = Modifier,
        visible = bottomSheetDialog,
        cancelable = true,
        canceledOnTouchOutside = true,
        onDismissRequest = { bottomSheetDialog = false },
    ) {
        DialogContent {
            bottomSheetDialog = false
        }
    }

    CustomDialog(
        contentAlignment = Alignment.Center,
        dialogBackgroundColor = Color.Transparent,
        modifier = Modifier,
        visible = customDialog,
        cancelable = true,
        canceledOnTouchOutside = true,
        onDismissRequest = { customDialog = false },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
        ) {
            Text(
                text = "Swipe up to open sheet. Swipe down to dismiss.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DialogLaunchButton(
    text: String,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun DialogContent(onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.7f)
            .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer),
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            onClick = onDismissRequest,
        ) {
            Text(
                modifier = Modifier.padding(all = 4.dp),
                text = "dismissDialog",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MyDialog(
    alertDialog: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit,
) {
    if (alertDialog.value) {
        Dialog(
            onDismissRequest = {
                alertDialog.value = false
            },
            properties = properties,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenDialog(alertDialog: MutableState<Boolean>, content: @Composable () -> Unit) {
    if (alertDialog.value) {
        Dialog(
            onDismissRequest = {
                alertDialog.value = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                content.invoke()
            }
        }
    }
}
