package com.xueh.commonlib.ui.compose.google

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xueh.commonlib.ui.HomePage
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.compose.itemView


@Preview
@Composable
fun GoogleSamplePage(controller: NavHostController) {
    var str = listOf(
        HomePage.ItemData("PullRefreshSample", RouteConfig.PullRefreshSample),
        HomePage.ItemData("CustomPullRefreshSample", RouteConfig.CustomPullRefreshSample),
        HomePage.ItemData(
            "PullRefreshIndicatorTransformSample",
            RouteConfig.PullRefreshIndicatorTransformSample
        ),
    )
    Column{
        str.forEach {
            itemView(item = it.str, showimg = false) {
                controller.navigate(it.router)
            }
        }
    }
}

