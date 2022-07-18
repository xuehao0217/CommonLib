package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */
@Composable
fun PageTwo(navController: NavController, name: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "这是页面2")
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "我是$name")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            //点击返回页面1
            navController.popBackStack()
        }) {
            Text(
                text = "返回页面1",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

}