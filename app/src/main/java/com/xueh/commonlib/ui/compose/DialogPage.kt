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

private data class DialogPageUiState(
    val alertVisible: Boolean = false,
    val fullScreenVisible: Boolean = false,
    val bottomSheetDialog: Boolean = false,
    val customDialog: Boolean = false,
    val showBottomSheet: Boolean = false,
)

/**
 * 各类 Dialog / BottomSheet 演示（Material 3）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DialogPage() {
    var dialogUi by remember { mutableStateOf(DialogPageUiState()) }
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
        DialogLaunchButton(text = "Alert Dialog") {
            dialogUi = dialogUi.copy(alertVisible = true)
        }
        DialogLaunchButton(text = "FullScreenDialog") {
            dialogUi = dialogUi.copy(fullScreenVisible = true)
        }
        DialogLaunchButton(text = "BottomSheetDialog（业务封装）") {
            dialogUi = dialogUi.copy(bottomSheetDialog = true)
        }
        DialogLaunchButton(text = "CustomDialog") {
            dialogUi = dialogUi.copy(customDialog = true)
        }
        DialogLaunchButton(text = "ModalBottomSheet（M3）") {
            dialogUi = dialogUi.copy(showBottomSheet = true)
        }
    }

    MyDialog(
        visible = dialogUi.alertVisible,
        onDismissRequest = { dialogUi = dialogUi.copy(alertVisible = false) },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
        }
    }

    FullScreenDialog(
        visible = dialogUi.fullScreenVisible,
        onDismissRequest = { dialogUi = dialogUi.copy(fullScreenVisible = false) },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
        }
    }

    BottomSheetDialog(
        modifier = Modifier,
        visible = dialogUi.bottomSheetDialog,
        cancelable = true,
        canceledOnTouchOutside = true,
        onDismissRequest = { dialogUi = dialogUi.copy(bottomSheetDialog = false) },
    ) {
        DialogContent {
            dialogUi = dialogUi.copy(bottomSheetDialog = false)
        }
    }

    CustomDialog(
        contentAlignment = Alignment.Center,
        dialogBackgroundColor = Color.Transparent,
        modifier = Modifier,
        visible = dialogUi.customDialog,
        cancelable = true,
        canceledOnTouchOutside = true,
        onDismissRequest = { dialogUi = dialogUi.copy(customDialog = false) },
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

    if (dialogUi.showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { dialogUi = dialogUi.copy(showBottomSheet = false) },
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
    visible: Boolean,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
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
