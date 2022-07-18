package com.xueh.commonlib.ui.compose

import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.weight.compose.*
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */
@Preview
@Composable
fun lazyVerticalGrid() {
    val scope = rememberCoroutineScope()
    var images = mutableListOf<String>()
    (0..60).forEach {
        images.add(imageUrl)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        val listState = rememberLazyGridState()

        LazyVerticalGrid(state = listState, columns = GridCells.Fixed(3), content = {
            items(images.size) { index ->
                ImageLoadCompose(images[index])
            }
        })

        val showButton by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 }
        }
        AnimatedVisibility(visible = showButton) {
            Column(modifier = Modifier.padding(bottom = 10.dp, end = 10.dp)) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Text(
                        text = "回到顶部",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

