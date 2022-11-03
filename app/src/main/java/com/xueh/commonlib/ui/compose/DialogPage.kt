package com.xueh.commonlib.ui.compose

import android.util.Log
import android.view.Surface
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
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.weight.compose.BoxText
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
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (bt_text) = createRefs()
        BoxText(text = "Dialog",textColor=Color.White,
            modifier = Modifier
                .size(180.dp, 44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .constrainAs(bt_text) {
                    top.linkTo(parent.top,15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .click {
                    alertDialog.value = true
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
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MyDialog(alertDialog: MutableState<Boolean>, content: @Composable () -> Unit) {
    if (alertDialog.value) {
        Dialog(
            onDismissRequest = {
                alertDialog.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            content()
        }
    }
}