package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.xueh.comm_core.weight.compose.CommonPagingPage
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */
@Composable
fun RefreshLoadUse() {
    val viewModel: ComposeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val homeDatas = viewModel.getListDatas().collectAsLazyPagingItems()
    CommonPagingPage(homeDatas, emptyDataContent = {
        Box(modifier = Modifier.background(Color.Blue))
    }) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape)
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(50.dp)
                .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = it.title,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}