package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import com.xueh.comm_core.helper.isEmpty


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
    stateLoading: @Composable () -> Unit = { PagingLoadingItem() },
    /** 加载错误 */
    stateError: @Composable (Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorContent {
            retry.invoke()
        }
    },
    /** 没有更多数据 */
    stateNoMore: @Composable () -> Unit = {
        PagingNoMoreItem()
    },
) {
    if (itemCount == 0) {
        when (val loadState = loadState.refresh) {
            is LoadState.Loading -> stateLoading()
            is LoadState.Error -> stateError(loadState.error) { retry() }
            is LoadState.NotLoading -> stateNoMore()
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
    stateLoading: @Composable () -> Unit = { PagingLoadingItem() },
    /** 加载错误 */
    stateError: @Composable (Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorContent {
            retry.invoke()
        }
    },
    /** 没有更多数据 */
    stateNoMore: @Composable () -> Unit = {
        PagingNoMoreItem()
    },
) {
    if (isStateAppend()) {
        when (val loadState = loadState.append) {
            is LoadState.Loading -> stateLoading()
            is LoadState.Error -> stateError(loadState.error) { retry() }
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
    stateLoading: @Composable () -> Unit = { PagingLoadingItem() },
    /** 加载错误 */
    stateError: @Composable (Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorContent {
            retry.invoke()
        }
    },
) {
    when (val loadState = loadState.prepend) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(loadState.error) { retry() }
        else -> {}
    }
}


//-------------------- EXT --------------------

@Composable
fun LazyPagingItems<*>.PagingBaseBox(
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    content: @Composable (LazyPagingItems<*>) -> Unit
) {
    val isRefreshing = isRefreshing()
    val err = isRefreshError()
    if (err) {
        if (pagingErrorContent != null) {
            pagingErrorContent.invoke { retry() }
        } else {
            PagingErrorContent { retry() }
        }
    }

    //第一次进入页面的时候 loading
    if (itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            if (isRefreshing) {
                if (pagingLoadingContent.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(50.dp)
                    )
                } else {
                    pagingLoadingContent?.invoke(this)
                }
            } else {
                if (pagingEmptyContent.isEmpty()) {
                    Text(text = "无数据")
                } else {
                    pagingEmptyContent?.invoke(this)
                }
            }
        }
    } else {
        content(this)
    }
}


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun <T : Any> LazyPagingItems<T>.PagingRefresh(
    headerIndicator: @Composable AnimatedVisibilityScope.() -> Unit,
    content: @Composable ColumnScope.(LazyPagingItems<T>) -> Unit
) {
    val isRefreshing = isRefreshing()
    val state = rememberPullRefreshState(isRefreshing, onRefresh = {
        refresh()
    })
    Column(
        Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        AnimatedVisibility(
            visible = isRefreshing, enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically(),
        ) {
            headerIndicator()
        }
        content(this@PagingRefresh)
    }
}


fun LazyListScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = "paging append ui state",
    contentType: Any? = "paging append ui state",
    content: @Composable LazyItemScope.() -> Unit = { items.PagingStateAppend() },
) {
    if (items.isStateAppend()) {
        item(
            key = key,
            contentType = contentType,
            content = content,
        )
    }
}

fun LazyGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = "paging append ui state",
    contentType: Any? = "paging append ui state",
    span: (LazyGridItemSpanScope.() -> GridItemSpan)? = { GridItemSpan(maxLineSpan) },
    content: @Composable LazyGridItemScope.() -> Unit = { items.PagingStateAppend() },
) {
    if (items.isStateAppend()) {
        item(
            key = key,
            contentType = contentType,
            span = span,
            content = content,
        )
    }
}

fun LazyStaggeredGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = "paging append ui state",
    contentType: Any? = "paging append ui state",
    span: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    content: @Composable LazyStaggeredGridItemScope.() -> Unit = { items.PagingStateAppend() }
) {
    if (items.isStateAppend()) {
        item(key = key, contentType = contentType, span = span, content = content)
    }
}

//-------------------- widget --------------------
@Composable
fun PagingErrorContent(retry: () -> Unit) {
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
fun PagingErrorItem(retry: () -> Unit) {
    Button(
        onClick = { retry() },
        modifier = Modifier.padding(10.dp),
//        colors = buttonColors(backgroundColor = AppTheme.colors.themeUi)
    ) {
        Text(text = "重试")
    }
}


@Composable
fun PagingNoMoreItem() {
    Text(
        text = "没有更多了",
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}


@Composable
fun PagingLoadingItem() {
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