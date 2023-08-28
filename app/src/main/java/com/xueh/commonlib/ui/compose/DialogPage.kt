package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.xueh.comm_core.weight.compose.BottomSheetDialog
import com.xueh.comm_core.weight.compose.BoxText
import com.xueh.comm_core.weight.compose.CustomDialog
import com.xueh.comm_core.weight.compose.click

/**
 * 创 建 人: xueh
 * 创建日期: 2022/10/28
 * 备注：
 */
@Preview
@Composable
fun DialogPage() {
    val alertDialog = remember { mutableStateOf(false) }
    val fullScreenDialog = remember { mutableStateOf(false) }
    var bottomSheetDialog by remember { mutableStateOf(false) }
    var customDialog by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(15.dp))
        BoxText(text = "Dialog",
            textColor = Color.White,
            modifier = Modifier
                .size(180.dp, 44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .click {
                    alertDialog.value = true
                })
        Spacer(modifier = Modifier.height(15.dp))
        BoxText(text = "FullScreenDialog",
            textColor = Color.White,
            modifier = Modifier
                .size(180.dp, 44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .click {
                    fullScreenDialog.value = true
                })
        Spacer(modifier = Modifier.height(15.dp))
        BoxText(text = "BottomSheetDialog",
            textColor = Color.White,
            modifier = Modifier
                .size(180.dp, 44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .click {
                    bottomSheetDialog = true
                })
        Spacer(modifier = Modifier.height(15.dp))
        BoxText(text = "CustomDialog",
            textColor = Color.White,
            modifier = Modifier
                .size(180.dp, 44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .click {
                    customDialog = true
                })
    }


    MyDialog(alertDialog) {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)) {

        }
    }

    FullScreenDialog(fullScreenDialog) {
        ConstraintLayout(modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)) {

        }
    }

    BottomSheetDialog(modifier = Modifier, visible = bottomSheetDialog, cancelable = true, canceledOnTouchOutside = true, onDismissRequest = {
        bottomSheetDialog = false
    }) {
        DialogContent {
            bottomSheetDialog = false
        }
    }

    CustomDialog(contentAlignment = Alignment.Center, dialogBackgroundColor = Color.Transparent, modifier = Modifier, visible = customDialog, cancelable = true, canceledOnTouchOutside = true, onDismissRequest = {
        customDialog = false
    }) {
        ConstraintLayout(modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)) {

        }
    }

}

@Composable
private fun DialogContent(onDismissRequest: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(fraction = 0.7f)
        .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(color = Color(0xFF009688)), verticalArrangement = Arrangement.Center) {
        Button(modifier = Modifier.align(alignment = Alignment.CenterHorizontally), onClick = {
            onDismissRequest()
        }) {
            Text(modifier = Modifier.padding(all = 4.dp), text = "dismissDialog", fontSize = 16.sp)
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


/**
 *
 * 全屏的 dialog 显示
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenDialog(alertDialog: MutableState<Boolean>, content: @Composable () -> Unit) {
    if (alertDialog.value) {
        Dialog(
            onDismissRequest = {
                alertDialog.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                content.invoke()
            }
        }
    }
}
