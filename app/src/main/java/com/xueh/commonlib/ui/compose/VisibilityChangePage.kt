package com.xueh.commonlib.ui.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun VisibilityChangedDemo() {
    val list = (1..100).toList()
    val listState = remember { LazyListState() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { isScrolling -> !isScrolling }
            .collect {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                Log.d("VisibilityChangedDemo", "---- 滚动停止 ----")
                visibleItems.forEach { info ->
                    Log.d(
                        "VisibilityChangedDemo",
                        "item ${info.index} 可见，高度: ${info.size}，位置: ${info.offset}",
                    )
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        DemoScreenIntro(
            text = "列表项绑定首次可见与可见比例回调；滚动停止时 Log 输出可见项信息。",
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                items = list,
                key = { it },
            ) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .onFirstVisible {
                            Log.i("FirstVisible", "Item $item 第一次出现在屏幕上")
                        }
                        .onVisibilityChanged(
                            minFractionVisible = 0.6f,
                            minDurationMs = 500,
                        ) { visible ->
                            Log.d("Visibility", "Item $item 可见比例: $visible")
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp,
                    ),
                ) {
                    Text(
                        text = "Item $item",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(20.dp),
                    )
                }
            }
        }
    }
}
