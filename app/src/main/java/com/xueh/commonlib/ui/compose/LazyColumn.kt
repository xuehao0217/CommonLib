package com.xueh.commonlib.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.weight.compose.ImageLoadCompose
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */
@Composable
fun LazyColumnPage(viewModel: ComposeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    viewModel.loadDsl()
    val bannerDatas by viewModel.bannerLiveData.observeAsState()
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        var isScroll by remember {
            mutableStateOf(false)
        }

        if (listState.isScrollInProgress) {
            DisposableEffect(Unit) {
                isScroll = true
                onDispose {
                    isScroll = false
                }
            }
        }

        val showButton by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 && !isScroll }
        }

        androidx.compose.foundation.lazy.LazyColumn(state = listState) {
            itemsIndexed(viewModel.bannerMutableState) { _, item ->
                itemView(item.title) {
                    ToastUtils.showShort("点击了 ${item.title}")
                }
            }
//                    items(viewModel.bannerMutableState.size) { index ->
//
//                    }
        }


        AnimatedVisibility(visible = showButton, modifier = Modifier.padding(15.dp)) {
            FloatingActionButton(
                modifier = Modifier.size(40.dp),
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Text(text = "置顶", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

var imageUrl =
    "https://c-ssl.dtstatic.com/uploads/item/202105/29/20210529001057_aSeLB.thumb.1000_0.jpeg"

@Composable
fun itemView(item: String, showimg: Boolean = true, clickEvent: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 15.dp)) {
        if (showimg) {
            ImageLoadCompose(imageUrl)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                .clickable(onClick = clickEvent)
                .fillMaxWidth()
                .height(50.dp)
                .border(1.5.dp,
                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }

}