package com.xueh.comm_core.widget

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList

@Composable
private fun <T : Any> LazyPagingItems<T>.ObserveVisibleItemsOnScrollStop(
    onStop: ((List<T>) -> Unit)?,
    observeBlock: @Composable (onIndexes: (List<Int>) -> Unit) -> Unit,
) {
    if (onStop == null) return
    observeBlock { visibleIndexes ->
        itemSnapshotList.items
            .filterIndexed { index, _ -> index in visibleIndexes }
            .let { onStop(it) }
    }
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingLazyColumn(
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    headContent: (@Composable () -> Unit)? = null,
    footContent: (@Composable () -> Unit)? = null,
    pagingAppendStateContent: @Composable () -> Unit = { PagingStateAppend() },
    pagingRefreshStateContent: @Composable () -> Unit = { PagingStateRefresh() },
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    ObserveVisibleItemsOnScrollStop(onStop = onScrollStopVisibleList) { callback ->
        lazyListState.onScrollStopVisibleList(callback)
    }

    CommonLazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        footContent = footContent,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingLazyColumn[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingLazyColumn) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    pagingAppendStateContent: @Composable () -> Unit = { PagingStateAppend() },
    pagingRefreshStateContent: @Composable () -> Unit = { PagingStateRefresh() },
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit,
) {
    ObserveVisibleItemsOnScrollStop(onStop = onScrollStopVisibleList) { callback ->
        state.onScrollStopVisibleList(callback)
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingVerticalGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    pagingAppendStateContent: @Composable () -> Unit = { PagingStateAppend() },
    pagingRefreshStateContent: @Composable () -> Unit = { PagingStateRefresh() },
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit,
) {
    ObserveVisibleItemsOnScrollStop(onStop = onScrollStopVisibleList) { callback ->
        state.onScrollStopVisibleList(callback)
    }

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalItemSpacing = verticalItemSpacing,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingVerticalStaggeredGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalStaggeredGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalPager(
    state: PagerState = rememberPagerState { itemCount },
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pagingRefreshStateContent: @Composable () -> Unit = { PagingStateRefresh() },
    pageContent: @Composable PagerScope.(T) -> Unit,
) {
    LaunchedEffect(itemCount) {
        if (itemCount > 0 && state.currentPage >= itemCount) {
            state.animateScrollToPage(itemCount - 1)
        }
    }
    VerticalPager(
        modifier = modifier,
        state = state,
        key = key,
        beyondViewportPageCount = beyondViewportPageCount,
    ) { index ->
        this@PagingVerticalPager[index]?.let { pageContent(it) }
    }
    pagingRefreshStateContent()
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingRefresh(
    modifier: Modifier = Modifier,
    refreshHeader: @Composable ((isRefreshing: Boolean) -> Unit)? = null,
    onContent: @Composable (LazyPagingItems<T>) -> Unit,
) {
    val state = rememberUltraSwipeRefreshState()
    val isRefreshing = isRefreshing()
    val currentItems = rememberUpdatedState(onContent)

    LaunchedEffect(isRefreshing) {
        state.isRefreshing = isRefreshing
    }

    UltraSwipeRefresh(
        state = state,
        loadMoreEnabled = false,
        onRefresh = { refresh() },
        onLoadMore = {},
        modifier = modifier,
        headerScrollMode = NestedScrollMode.Translate,
        footerScrollMode = NestedScrollMode.Translate,
        headerIndicator = {
            refreshHeader?.invoke(isRefreshing) ?: ClassicRefreshHeader(it)
        },
        footerIndicator = {},
    ) {
        currentItems.value(this)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T : Any> LazyPagingItems<T>.PagingRefreshColumn(
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    refreshHeader: @Composable AnimatedVisibilityScope.() -> Unit = {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
    onContent: @Composable BoxScope.(LazyPagingItems<T>) -> Unit,
) {
    val isRefreshing = isRefreshing()
    Column(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .pullToRefresh(state = state, isRefreshing = isRefreshing) { refresh() },
    ) {
        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically(),
        ) {
            refreshHeader()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            onContent(this@PagingRefreshColumn)
        }
    }
}
