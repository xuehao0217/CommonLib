package com.xueh.comm_core.weight.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader

@Composable
fun <T : Any> LazyPagingItems<T>.PagingRefreshList(
    isFirstRefresh: Boolean = true,
    key: ((index: Int) -> Any)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
    headContent: @Composable () -> Unit = {},
    foodContent: @Composable () -> Unit = {},
    pagingRefreshStateContent: @Composable (() -> Unit) = { PagingStateRefresh() },
    pagingAppendStateContent: @Composable (() -> Unit) = { PagingStateAppend() },
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    //是不是在loading
    val isRefreshing = isRefreshing()
    SmartRefresh(
        isFirstRefresh = isFirstRefresh,
        isRefreshing = isRefreshing,
        scrollState = lazyListState,
        refreshState = refreshState,
        headerIndicator = headerIndicator,
        onRefresh = {
            refresh()
        }) {
        PagingLazyColumn(
            lazyListState = lazyListState,
            key = key,
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding,
            pagingRefreshStateContent = pagingRefreshStateContent,
            pagingAppendStateContent = pagingAppendStateContent,
            onScrollStopVisibleList=onScrollStopVisibleList,
            headContent = headContent,
            foodContent = foodContent,
            itemContent = itemContent
        )
    }
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingLazyColumn(
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
    key: ((index: Int) -> Any)? = null,
    headContent: @Composable () -> Unit = {},
    foodContent: @Composable () -> Unit = {},
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    pagingRefreshStateContent: @Composable (() -> Unit) = { PagingStateRefresh() },
    pagingAppendStateContent: @Composable (() -> Unit) = { PagingStateAppend() },
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (onScrollStopVisibleList != null) {
        lazyListState.onScrollStopVisibleList {
            if (itemSnapshotList.items.isNotEmpty()) {
                itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    CommonLazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        // 如果是老版本的Paging3这里的实现方式不同，自己根据版本来实现。
        items(itemCount, key = key) { index ->
            this@PagingLazyColumn[index]?.let {
                itemContent(it)
            }
        }
        PagingAppendItem(this@PagingLazyColumn) {
            pagingAppendStateContent()
        }
    }
    pagingRefreshStateContent()
}


@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),
    pagingRefreshStateContent: @Composable (() -> Unit) = { PagingStateRefresh() },
    pagingAppendStateContent: @Composable (() -> Unit) = { PagingStateAppend() },
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList {
            if (itemSnapshotList.items.isNotEmpty()) {
                itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns), state = state, contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        items(
            count = itemCount,
            key = key,
        ) {
            this@PagingVerticalGrid[it]?.let {
                itemContent(it)
            }
        }
        PagingAppendItem(this@PagingVerticalGrid) {
            pagingAppendStateContent()
        }
    }
    pagingRefreshStateContent()
}


@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    pagingRefreshStateContent: @Composable (() -> Unit) = { PagingStateRefresh() },
    pagingAppendStateContent: @Composable (() -> Unit) = { PagingStateAppend() },
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList {
            if (itemSnapshotList.items.isNotEmpty()) {
                itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement
    ) {
        items(
            count = itemCount,
            key = key,
        ) {
            this@PagingVerticalStaggeredGrid[it]?.let {
                itemContent(it)
            }
        }
        PagingAppendItem(this@PagingVerticalStaggeredGrid) {
            pagingAppendStateContent()
        }
    }
    pagingRefreshStateContent()
}


/*
*  val pagerState = rememberPagerState { lazyPagingItems.itemCount }
* */
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalPager(
    state: PagerState = rememberPagerState { itemCount },
    modifier: Modifier = Modifier,
    pagingRefreshStateContent: @Composable (() -> Unit) = { PagingStateRefresh() },
    key: ((index: Int) -> Any)? = null,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageContent: @Composable PagerScope.(T) -> Unit
) {
    VerticalPager(
        modifier = modifier,
        state = state,
        beyondViewportPageCount=beyondViewportPageCount,
        key = key
    ) { index ->
        this@PagingVerticalPager[index]?.let {
            pageContent(it)
        }
    }
    pagingRefreshStateContent()
}

//--------------------------------------------------------------------------------------------------
@Composable
fun <T : Any> LazyPagingItems<T>.SmartRefreshPaging(
    isFirstRefresh: Boolean = true,
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    content: @Composable () -> Unit,
) {
    //是不是在loading
    val isRefreshing = isRefreshing()
    SmartRefresh(
        isFirstRefresh = isFirstRefresh,
        isRefreshing = isRefreshing,
        refreshState = refreshState,
        headerIndicator = headerIndicator,
        onRefresh = {
            refresh()
        }) {
        content()
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T : Any> LazyPagingItems<T>.PagingRefreshColumn(
    state: PullToRefreshState = rememberPullToRefreshState(),
    headerIndicator: @Composable AnimatedVisibilityScope.() -> Unit,
    content: @Composable BoxScope.(LazyPagingItems<T>) -> Unit
) {
    val isRefreshing = isRefreshing()
    Column(
        Modifier.fillMaxSize().animateContentSize().pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = {
            this.refresh()
        }),
    ) {
        AnimatedVisibility(
            visible = isRefreshing, enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically(),
        ) {
            headerIndicator()
        }
        Box(modifier = Modifier
            .weight(1F)
            .fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            content(this@PagingRefreshColumn)
        }
    }
}
