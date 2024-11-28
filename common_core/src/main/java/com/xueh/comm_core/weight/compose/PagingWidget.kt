package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.helper.isEmpty
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader

//公用 下拉刷新下拉加载 页面
@Composable
fun <T : Any> CommonPagingPage(
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    isFirstRefresh: Boolean = true,
    enableRefresh: Boolean = true,

    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),

    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    onScrollStop: ((visibleItem: List<Int>, isScrollingUp: Boolean) -> Unit)? = null,

    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,

    key: ((index: Int) -> Any)? = null,

    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},

    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    var lastFirstIndex by rememberMutableStateOf { 0 }
    var isScrollingUp = false
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                isScrollingUp = if (lazyListState.firstVisibleItemIndex > lastFirstIndex) {
                    // 上滑
                    true
                } else {
                    //下滑
                    false
                }
                lastFirstIndex = lazyListState.firstVisibleItemIndex
                // 滑动停止
                val visibleItemsIndex =
                    lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toList()
                onScrollStop?.invoke(visibleItemsIndex, isScrollingUp)
            }
        }
    }
    RefreshList(
        enableRefresh = enableRefresh,
        isFirstRefresh = isFirstRefresh,
        lazyListState = lazyListState,
        refreshState = refreshState,
        key = key,
        lazyPagingItems = lazyPagingItems,
        headerIndicator = headerIndicator,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        emptyDataContent = emptyDataContent,
        loadingContent = loadingContent,
        headContent = headContent,
        foodContent = foodContent,
        itemContent = itemContent,
    )
}


@Composable
fun <T : Any> RefreshList(
    enableRefresh: Boolean = true,
    isFirstRefresh: Boolean = true,
    lazyPagingItems: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
//    //是不是在loading
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    PagingBaseBox(
        lazyPagingItems = lazyPagingItems,
        emptyDataContent = emptyDataContent,
        loadingContent = loadingContent
    ) {
        if (!enableRefresh) {
            PagingCommonLazyColumn(
                lazyPagingItems = lazyPagingItems,
                lazyListState = lazyListState,
                key = key,
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
                    key = key,
                    verticalArrangement = verticalArrangement,
                    contentPadding = contentPadding,
                    headContent = headContent,
                    foodContent = foodContent,
                    itemContent = itemContent
                )
            }
        }

    }

}

@Composable
fun <T : Any> PagingCommonLazyColumn(
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    key: ((index: Int) -> Any)? = null,
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    //刷新状态
    CommonLazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        // 如果是老版本的Paging3这里的实现方式不同，自己根据版本来实现。
        items(lazyPagingItems.itemCount, key = key) { index ->
            lazyPagingItems[index]?.let {
                itemContent(it)
            }
        }
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


/*
*  key = lazyPagingItems.itemKey { it.id }
*  val pagerState = rememberPagerState { lazyPagingItems.itemCount }
* */
@Composable
fun <T : Any> PagingVerticalPager(
    lazyPagingItems: LazyPagingItems<T>,
    state: PagerState = rememberPagerState { lazyPagingItems.itemCount },
    modifier: Modifier = Modifier,
    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,
    key: ((index: Int) -> Any)? = null,
    pageContent: @Composable PagerScope.(T) -> Unit
) {
    PagingBaseBox(lazyPagingItems, modifier = modifier, emptyDataContent, loadingContent) {
        VerticalPager(
            state = state,
            key = key
        ) { index ->
            lazyPagingItems[index]?.let {
                pageContent(it)
            }
        }
    }
}


/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalGrid(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),

    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,

    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit
) {
    PagingBaseBox(lazyPagingItems, modifier = modifier, emptyDataContent, loadingContent) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns), state = state, contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = key,
            ) {
                lazyPagingItems[it]?.let {
                    itemContent(it)
                }
            }
            item(span = { GridItemSpan(columns) }) { // Use the span variable here
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
}


/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalStaggeredGrid(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,
    columns: Int = 2,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit
) {
    PagingBaseBox(lazyPagingItems, modifier = modifier, emptyDataContent, loadingContent) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columns),
            state = state,
            contentPadding = contentPadding,
            verticalItemSpacing = verticalItemSpacing,
            horizontalArrangement = horizontalArrangement
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = key,
            ) {
                lazyPagingItems[it]?.let {
                    itemContent(it)
                }
            }
            item(span = StaggeredGridItemSpan.FullLine) {
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
}


@Composable
fun <T : Any> PagingBaseBox(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    //错误页
    val err = lazyPagingItems.loadState.refresh is LoadState.Error
    if (err) {
        ErrorContent { lazyPagingItems.retry() }
        return
    }
    Box(modifier = modifier) {
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
                    } else {
                        emptyDataContent?.invoke(this)
                    }
                }
            }
        }
        content()
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
