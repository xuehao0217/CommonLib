package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
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
        lazyPagingItems = lazyPagingItems,
        headerIndicator = headerIndicator,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        emptyDataContent = emptyDataContent,
        loadingContent = loadingContent,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        // 如果是老版本的Paging3这里的实现方式不同，自己根据版本来实现。
        items(lazyPagingItems.itemCount, key = key) { index ->
            lazyPagingItems[index]?.let {
                itemContent(it)
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
    key: ((index: Int) -> Any)? = null,
    pageContent: @Composable PagerScope.(T) -> Unit
) {
    VerticalPager(
        modifier = modifier,
        state = state,
        key = key
    ) { index ->
        lazyPagingItems[index]?.let {
            pageContent(it)
        }
    }
}


/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalGrid(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit
) {
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
    }
}



/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalStaggeredGrid(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int = 2,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns), state = state, contentPadding = contentPadding,
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
    }
}