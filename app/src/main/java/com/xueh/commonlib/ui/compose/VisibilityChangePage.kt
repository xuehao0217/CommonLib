package com.xueh.commonlib.ui.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun VisibilityChangedDemo() {
    val list = (1..100).toList()
    val listState = remember { LazyListState() }

    /**
     * 监听滚动状态，当滚动结束（isScrollInProgress == false）时，
     * 输出当前所有可见 item 的信息
     */
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged() // 只在状态变化时触发
            .filter { isScrolling -> !isScrolling } // 滚动结束时触发
            .collect {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                Log.d("VisibilityChangedDemo", "---- 滚动停止 ----")
                visibleItems.forEach { info ->
                    Log.d(
                        "VisibilityChangedDemo",
                        "item ${info.index} 可见，高度: ${info.size}，位置: ${info.offset}"
                    )
                }
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(list) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(8.dp)
                    .background(Color(0xFFC8E6C9))
                    .onFirstVisible {
                        Log.i("FirstVisible", "Item $item 第一次出现在屏幕上")
                    }
                    .onVisibilityChanged(
                        minFractionVisible = 0.6f,
                        minDurationMs = 500,
                    ) { visible ->
                        Log.d("Visibility", "Item $item 可见比例: $visible")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Item $item", fontSize = 18.sp)
            }
        }
    }
}
