package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

//-------------------- Refresh --------------------

/**
 * 判断当前 LazyPagingItems 是否处于刷新状态
 */
fun LazyPagingItems<*>.isRefreshing() = loadState.refresh is LoadState.Loading

/**
 * 判断当前 LazyPagingItems 是否刷新失败
 */
fun LazyPagingItems<*>.isRefreshError() = loadState.refresh is LoadState.Error

/**
 * 根据 [loadState.refresh] 状态展示 UI
 * @param stateLoading 加载中显示内容
 * @param stateError 加载错误显示内容
 * @param stateEmpty 数据为空显示内容
 */
@Composable
fun LazyPagingItems<*>.PagingStateRefresh(
    stateLoading: @Composable () -> Unit = { PagingRefreshLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingRefreshError(this) },
    stateEmpty: @Composable () -> Unit = { PagingRefreshEmpty() },
) {
    // 仅在没有数据时显示状态页
    if (itemCount == 0) {
        when (val refreshState = loadState.refresh) {
            is LoadState.Loading -> stateLoading()
            is LoadState.Error -> stateError(refreshState.error)
            is LoadState.NotLoading -> stateEmpty()
        }
    }
}

//-------------------- Append --------------------

/**
 * 是否可以显示加载更多状态
 */
fun LazyPagingItems<*>.isStateAppend() = itemCount > 0

/**
 * 根据 [loadState.append] 状态展示加载更多 UI
 * @param stateLoading 加载中显示
 * @param stateError 加载错误显示
 * @param stateNoMore 没有更多显示
 */
@Composable
fun LazyPagingItems<*>.PagingStateAppend(
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingAppendError(this) },
    stateNoMore: @Composable () -> Unit = { PagingAppendNoMore() },
) {
    if (!isStateAppend()) return
    when (val appendState = loadState.append) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(appendState.error)
        is LoadState.NotLoading -> if (appendState.endOfPaginationReached && itemCount > 0) stateNoMore()
    }
}

//-------------------- Prepend --------------------

/**
 * 是否可以显示前置加载状态
 */
fun LazyPagingItems<*>.isStatePrepend(): Boolean =
    itemCount > 0 && !isRefreshing() && !loadState.prepend.endOfPaginationReached

/**
 * 根据 [loadState.prepend] 状态展示前置加载 UI
 * @param stateLoading 加载中显示
 * @param stateError 加载错误显示
 */
@Composable
fun LazyPagingItems<*>.PagingStatePrepend(
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingAppendError(this) },
) {
    when (val prependState = loadState.prepend) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(prependState.error)
        else -> {}
    }
}

//-------------------- LazyListScope / LazyGridScope / LazyStaggeredGridScope Append Item --------------------

/**
 * 在 LazyColumn 中添加加载更多 Item
 */
fun LazyListScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, content = content)
}

/**
 * 在 LazyGrid 中添加加载更多 Item
 */
fun LazyGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: (LazyGridItemSpanScope.() -> GridItemSpan) = { GridItemSpan(maxLineSpan) },
    content: @Composable LazyGridItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, span = span, content = content)
}

/**
 * 在 LazyStaggeredGrid 中添加加载更多 Item
 */
fun LazyStaggeredGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    content: @Composable LazyStaggeredGridItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, span = span, content = content)
}

//-------------------- Refresh Widgets --------------------

/**
 * 刷新失败 UI
 */
@Composable
fun PagingRefreshError(item: LazyPagingItems<*>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.stat_notify_error),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Red)
            )
            Text(text = "请求出错啦", modifier = Modifier.padding(top = 10.dp))
            Button(onClick = { item.retry() }, modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "重试")
            }
        }
    }
}

/**
 * 刷新加载中 UI
 */
@Composable
fun PagingRefreshLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

/**
 * 刷新空数据 UI
 */
@Composable
fun PagingRefreshEmpty() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "无数据")
    }
}

//-------------------- Append Widgets --------------------

/**
 * 加载更多失败 UI
 */
@Composable
fun PagingAppendError(item: LazyPagingItems<*>) {
    Text(
        text = "加载失败",
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { item.retry() },
        textAlign = TextAlign.Center
    )
}

/**
 * 没有更多数据 UI
 */
@Composable
fun PagingAppendNoMore() {
    Text(
        text = "没有更多了",
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        textAlign = TextAlign.Center
    )
}

/**
 * 加载更多中 UI
 */
@Composable
fun PagingAppendLoading() {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}
