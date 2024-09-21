package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.helper.isEmpty
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader


@Composable
fun <T : Any> RefreshList(
    enableRefresh: Boolean = true,
    isFirstRefresh: Boolean = true,
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState)},
    itemContent: LazyListScope.() -> Unit,
) {
    //是不是在loading
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    //错误页
    val err = lazyPagingItems.loadState.refresh is LoadState.Error

    if (err) {
        ErrorContent { lazyPagingItems.retry() }
        return
    }

    //第一次进入页面的时候 loading
    if (lazyPagingItems.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            if (isRefreshing) {
                if (loadingContent.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(50.dp)
                    )
                } else {
                    loadingContent?.invoke(this)
                }
            } else {
                if (emptyDataContent.isEmpty()) {
                    Text(text = "无数据")
                }else{
                    emptyDataContent?.invoke(this)
                }
            }
        }
    }

    if (!enableRefresh) {
        PagingCommonLazyColumn(
            lazyPagingItems = lazyPagingItems,
            lazyListState = lazyListState,
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding,
            headContent = headContent,
            foodContent = foodContent,
            itemContent = itemContent
        )
    } else {
        SmartRefresh(
            isFirstRefresh = isFirstRefresh,
            isRefreshing = isRefreshing,
            scrollState = lazyListState,
            refreshState = refreshState,
            headerIndicator = headerIndicator,
            onRefresh = {
                lazyPagingItems.refresh()
            }) {
            PagingCommonLazyColumn(
                lazyPagingItems = lazyPagingItems,
                lazyListState = lazyListState,
                verticalArrangement = verticalArrangement,
                contentPadding = contentPadding,
                headContent = headContent,
                foodContent = foodContent,
                itemContent = itemContent
            )
        }
    }

}

@Composable
fun <T : Any> PagingCommonLazyColumn(
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    itemContent: LazyListScope.() -> Unit,
) {
    //刷新状态
    CommonLazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        itemContent()
        item {
            lazyPagingItems.apply {
                when (loadState.append) {
                    is LoadState.Loading -> LoadingItem()
                    is LoadState.Error -> ErrorItem { retry() }
                    is LoadState.NotLoading -> {
                        if (loadState.append.endOfPaginationReached) {
                            //只有大于0 才显示 不然就是空数据状态页
                            if (lazyPagingItems.itemCount > 0) {
                                NoMoreItem()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorContent(retry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = painterResource(id = R.drawable.stat_notify_error),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Red),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "请求出错啦",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
            Button(
                onClick = { retry() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
//                colors = buttonColors(backgroundColor = AppTheme.colors.themeUi)
            ) {
                Text(text = "重试")
            }
        }
    }
}

@Composable
fun ErrorItem(retry: () -> Unit) {
    Button(
        onClick = { retry() },
        modifier = Modifier.padding(10.dp),
//        colors = buttonColors(backgroundColor = AppTheme.colors.themeUi)
    ) {
        Text(text = "重试")
    }
}

@Composable
fun NoMoreItem() {
    Text(
        text = "没有更多了",
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
//            color = AppTheme.colors.themeUi,
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
        )
    }
}
