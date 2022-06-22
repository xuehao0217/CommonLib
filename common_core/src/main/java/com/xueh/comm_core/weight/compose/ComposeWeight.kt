package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.CommonLibTheme


@Composable
fun ComposeTitleView(
    title: String,
    backClick: () -> Unit,
    rightView: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.bar_arrows_left_black),
            contentDescription = "返回图标",
            modifier = Modifier
                .clickable {
                    backClick.invoke()
                },
        )
        Text(text = title, fontSize = 18.sp)
        Box {
            rightView?.invoke()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ComposeTitleViewPreview() {
    CommonLibTheme {
        ComposeTitleView("Android", {

        })
    }
}