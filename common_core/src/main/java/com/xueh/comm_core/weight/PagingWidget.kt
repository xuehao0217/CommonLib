package com.xueh.comm_core.weight

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

// ==========================================
// 内部公共：滚动停止可见项监听
// ==========================================

/**
 * 监听滚动停止后的可见项列表，将可见索引映射为实际数据并回调。
 * 三种列表组件（LazyColumn / Grid / StaggeredGrid）共享此逻辑。
 */
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

// ==========================================
// LazyColumn
// ==========================================

/**
 * Paging3 + LazyColumn 封装，支持下拉刷新与加载更多状态展示。
 *
 * 默认自动展示刷新状态页（加载中/错误/空数据）和加载更多状态，
 * 可通过 [pagingRefreshStateContent] / [pagingAppendStateContent] 自定义。
 *
 * @param contentType 用于 Compose 优化 item 复用池，不同类型的 item 不会被错误复用
 */
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
            contentType = { contentType?.invoke(it) }
        ) { index ->
            this@PagingLazyColumn[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingLazyColumn) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// LazyVerticalGrid
// ==========================================

/**
 * Paging3 + LazyVerticalGrid 封装，支持分页网格列表与刷新/加载更多状态。
 *
 * @param contentType 用于 Compose 优化 item 复用池
 */
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
        horizontalArrangement = horizontalArrangement
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) }
        ) { index ->
            this@PagingVerticalGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// LazyVerticalStaggeredGrid
// ==========================================

/**
 * Paging3 + LazyVerticalStaggeredGrid 封装，支持分页瀑布流布局。
 *
 * @param contentType 用于 Compose 优化 item 复用池
 */
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
        verticalItemSpacing = verticalItemSpacing
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) }
        ) { index ->
            this@PagingVerticalStaggeredGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalStaggeredGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// VerticalPager
// ==========================================

/**
 * Paging3 + VerticalPager 封装，支持分页数据的垂直翻页展示。
 *
 * 注意：默认 [state] 的 pageCount 通过 lambda 读取 [itemCount]，能自动感知 Paging 加载。
 * 如果调用方传入自定义 PagerState，需确保其 pageCount 与 LazyPagingItems.itemCount 保持同步，
 * 否则可能出现页数不一致导致的越界或空白页。
 */
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalPager(
    state: PagerState = rememberPagerState { itemCount },
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pagingRefreshStateContent: @Composable () -> Unit = { PagingStateRefresh() },
    pageContent: @Composable PagerScope.(T) -> Unit,
) {
    VerticalPager(
        modifier = modifier,
        state = state,
        key = key,
        beyondViewportPageCount = beyondViewportPageCount
    ) { index ->
        this@PagingVerticalPager[index]?.let { pageContent(it) }
    }
    pagingRefreshStateContent()
}

// ==========================================
// Refresh（UltraSwipeRefresh）
// ==========================================

/**
 * 基于 UltraSwipeRefresh 的分页刷新组件。
 *
 * 将下拉刷新与 Paging 的 refresh 能力集成，支持自定义刷新头。
 * Paging3 的加载更多由框架自动触发，因此禁用了 UltraSwipeRefresh 的上拉加载功能。
 */
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
        footerIndicator = {}
    ) {
        currentItems.value(this)
    }
}

// ==========================================
// Refresh（Material3 PullToRefresh）
// ==========================================

/**
 * 基于 Material3 PullToRefresh 的分页刷新列布局。
 *
 * 使用 M3 pullToRefresh 实现下拉刷新，与 Paging 的 refresh 联动。
 *
 * @param modifier 外部布局修饰符
 * @param refreshHeader 刷新头部内容，默认展示 CircularProgressIndicator
 */
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
            .pullToRefresh(state = state, isRefreshing = isRefreshing) { refresh() }
    ) {
        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically()
        ) {
            refreshHeader()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            onContent(this@PagingRefreshColumn)
        }
    }
}
