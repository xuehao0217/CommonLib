
package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.xueh.comm_core.weight.compose.*
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2023/3/23
 * 备注：
 */

@Preview
@Composable
fun CommonTabPage() {
    CommonTabPage(tabsName = mutableListOf("AAAAAAA", "BBBBBBB")) { index ->
        if (index == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Yellow)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
            )
        }
    }
}


@Preview
@Composable
fun CommonLazyColumnDatasPage() {
    val datas = mutableListOf<Int>()
    (0..30).forEach {
        datas.add(it)
    }
    CommonLazyColumnData(datas, headContent = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Red)
        )
    }, foodContent = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Yellow)
        )
    }) {
        BoxText(
            text = "${it}", modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview
@Composable
fun CommonRefresh() {
    var isRefresh by remember {
        mutableStateOf(false)
    }
    var scope = rememberCoroutineScope()
    CommonRefreshPage(isRefresh, onRefresh = {
        scope.launch {
            isRefresh = true
            delay(3000)
            isRefresh = false
        }
    }) {
        CommonLazyColumnDatasPage()
    }
}


@Preview
@Composable
fun CommonRefreshColumnData() {
    var isRefresh by remember {
        mutableStateOf(false)
    }
    val datas = remember {
        mutableStateListOf<Int>()
    }
    datas.addAll(10..20)
    var scope = rememberCoroutineScope()
    CommonRefreshColumnDataPage(datas, isRefresh, onRefresh = {
        scope.launch {
            isRefresh = true
            delay(3000)
            datas.clear()
            datas.addAll(1..10)
            isRefresh = false
        }
    }) {
        BoxText(
            text = "${it}", modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}


@Preview
@Composable
fun CommonPaging(viewModel: ComposeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    CommonPagingPage(viewModel.getTestDatas().collectAsLazyPagingItems()) {
        BoxText(
            text = "${it}", modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}