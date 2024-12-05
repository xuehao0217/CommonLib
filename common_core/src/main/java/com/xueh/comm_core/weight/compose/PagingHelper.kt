package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems


//-------------------- refresh --------------------
/**
 * 是否刷新中
 */
fun LazyPagingItems<*>.isRefreshing() = loadState.refresh is LoadState.Loading

/**
 * 是否刷新失败
 */
fun LazyPagingItems<*>.isRefreshError() = loadState.refresh is LoadState.Error

/**
 * [CombinedLoadStates.refresh]状态
 */
@Composable
inline fun LazyPagingItems<*>.PagingStateRefresh(
    /** 加载中 */
    stateLoading: @Composable () -> Unit = { PagingRefreshLoading() },
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = { error ->
        PagingRefreshError(this)
    },
    /** 没有数据 */
    stateEmpty: @Composable () -> Unit = {
        PagingRefreshEmpty()
    },
) {
    if (itemCount == 0) {
        when (val loadState = loadState.refresh) {
            is LoadState.Loading -> stateLoading()
            is LoadState.Error -> stateError(loadState.error)
            is LoadState.NotLoading -> stateEmpty()
        }
    }
}


//-------------------- append --------------------
/**
 * 是否显示[CombinedLoadStates.append]状态 也就是加载更多
 */
fun LazyPagingItems<*>.isStateAppend(): Boolean {
    return itemCount > 0
}

/**
 * [CombinedLoadStates.append]状态  也就是加载更多
 */
@Composable
inline fun LazyPagingItems<*>.PagingStateAppend(
    /** 加载中 */
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = { error ->
        PagingAppendError(this)
    },
    /** 没有更多数据 */
    stateNoMore: @Composable () -> Unit = {
        PagingAppendNoMore()
    },
) {
    if (isStateAppend()) {
        when (val loadState = loadState.append) {
            is LoadState.Loading -> stateLoading()
            is LoadState.Error -> stateError(loadState.error)
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    //只有大于0 才显示 不然就是空数据状态页
                    if (itemCount > 0) {
                        stateNoMore()
                    }
                }
            }
        }
    }
}


//-------------------- prepend --------------------
/**
 * 是否显示[CombinedLoadStates.prepend]状态
 */
fun LazyPagingItems<*>.isStatePrepend(): Boolean {
    if (itemCount <= 0) return false
    if (isRefreshing()) return false
    return !loadState.prepend.endOfPaginationReached
}

/**
 * [CombinedLoadStates.prepend]状态
 */
@Composable
inline fun LazyPagingItems<*>.PagingStatePrepend(
    /** 加载中 */
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = { error ->
        PagingAppendError(item = this)
    },
) {
    when (val loadState = loadState.prepend) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(loadState.error)
        else -> {}
    }
}

//-------------------- EXT --------------------
fun LazyListScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) {
        item(
            key = key ?: "append_item",
            contentType = contentType,
            content = content,
        )
    }
}

fun LazyGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: (LazyGridItemSpanScope.() -> GridItemSpan) = { GridItemSpan(maxLineSpan) },
    content: @Composable LazyGridItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) {
        item(
            key = key ?: "append_item",
            contentType = contentType,
            span = span,
            content = content,
        )
    }
}

fun LazyStaggeredGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    content: @Composable LazyStaggeredGridItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) {
        item(key = key ?: "append_item", contentType = contentType, span = span, content = content)
    }
}

//-------------------- widget  Refresh--------------------
@Composable
fun PagingRefreshError(item: LazyPagingItems<*>) {
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
                onClick = { item.retry() },
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
fun PagingRefreshLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
//            color = AppTheme.colors.themeUi,
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
        )
    }
}

@Composable
fun PagingRefreshEmpty() {
    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
        Text(text = "无数据")
    }
}

//-------------------- widget Append--------------------
@Composable
fun PagingAppendError(item: LazyPagingItems<*>) {
    Text(
        text = "加载失败",
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .click {
                item.retry()
            },
        textAlign = TextAlign.Center
    )
}

@Composable
fun PagingAppendNoMore() {
    Text(
        text = "没有更多了",
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun PagingAppendLoading() {
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

