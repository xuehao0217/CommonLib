package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer
import com.xueh.comm_core.helper.compose.rememberMutableStateOf
import com.xueh.comm_core.weight.compose.SpacerH
import com.xueh.commonlib.R
import kotlinx.coroutines.delay

/**
 * 创 建 人: xueh
 * 创建日期: 2023/7/17
 * 备注：
 */
@Composable
fun PlaceholderPage() {
    var visible by rememberMutableStateOf(value = true)
    Column {
        LaunchedEffect(Unit) {
            delay(5000)
            visible=false
        }
        SpacerH(int = 15)

        Text(
            text = "Content to display after content has loaded",
            modifier = Modifier
                .padding(16.dp)
                .placeholder(visible = visible)
        )

        SpacerH(int = 15)

        Text(
            text = "fade  Content to display after content has loaded",
            modifier = Modifier
                .padding(16.dp)
                .placeholder(
                    visible = visible,
                    highlight = PlaceholderHighlight.fade(),
                )
        )
        SpacerH(int = 15)

        Text(
            text = "shimmer  Content to display after content has loaded",
            modifier = Modifier
                .padding(16.dp)
                .placeholder(
                    visible = visible,
                    highlight = PlaceholderHighlight.shimmer(MaterialTheme.colorScheme.primary),
                )
        )
        SpacerH(int = 15)

        Image(
            painter = painterResource(id = R.mipmap.ic_head), contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
                .placeholder(
                    visible = visible,
                    highlight = PlaceholderHighlight.shimmer(MaterialTheme.colorScheme.primary),
                )
        )
    }

}