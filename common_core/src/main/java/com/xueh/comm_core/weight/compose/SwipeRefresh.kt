package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.ThresholdScrollStrategy
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader

/**
 * 创 建 人: xueh
 * 创建日期: 2022/9/29
 * 备注：
 */
@Composable
fun SmartRefresh(
    isRefreshing: Boolean = false,
    isNeedLoadMore: Boolean = false,
    isNeedRefresh: Boolean = true,
    isFirstRefresh: Boolean = true,
    scrollState: LazyListState = rememberLazyListState(),//滑动状态
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState().also {
        it.enableLoadMore = isNeedLoadMore
        it.enableRefresh = isNeedRefresh
    },//下拉刷新状态
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    footerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    onRefresh: (suspend () -> Unit)? = null,
    onLoadMore: (suspend () -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    with(LocalDensity.current) {
        refreshState.dragHeaderIndicatorStrategy = ThresholdScrollStrategy.UnLimited
        refreshState.dragFooterIndicatorStrategy = ThresholdScrollStrategy.Fixed(160.dp.toPx())
        refreshState.flingHeaderIndicatorStrategy = ThresholdScrollStrategy.None
        refreshState.flingFooterIndicatorStrategy = ThresholdScrollStrategy.Fixed(80.dp.toPx())
        refreshState.needFirstRefresh = isFirstRefresh
    }

    if (isRefreshing) {
        refreshState.refreshFlag = SmartSwipeStateFlag.REFRESHING
    } else {
        refreshState.refreshFlag = SmartSwipeStateFlag.SUCCESS
    }

    SmartSwipeRefresh(
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        state = refreshState,
        headerIndicator = headerIndicator,
        footerIndicator = footerIndicator,
        contentScrollState = scrollState,
        content = content
    )

}