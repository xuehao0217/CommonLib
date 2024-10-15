package com.xueh.commonlib.ui.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.helper.compose.rememberMutableStateOf
import com.xueh.comm_core.weight.compose.BoxText
import com.xueh.comm_core.weight.compose.CommonPagingPage
import com.xueh.comm_core.weight.compose.CommonTitleView
import com.xueh.comm_core.weight.compose.click
import com.xueh.commonlib.entity.HomeEntity
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */


@Composable
fun RefreshLoadUse() {
    BaseComposeViewModel<ComposeViewModel> { viewModel ->
        val homeDatas = viewModel.getListDatas().collectAsLazyPagingItems()

        val lazyListState = rememberLazyListState()

        //-------------------------------------------监听滑动第一个条目-------------------------------------------------------
        val firstVisibleScrollOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }
        val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        val targetHeight = BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(50f)
        var alpha by rememberMutableStateOf(value = 0f)
        LaunchedEffect(Unit) {
            snapshotFlow { firstVisibleScrollOffset }.collect {
                snapshotFlow { firstVisibleScrollOffset }.collect {
                    if (firstVisibleIndex <= 1) {
                        alpha = firstVisibleScrollOffset.toFloat() / targetHeight
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------

        var dataList by remember { mutableStateOf<List<HomeEntity.Data>>(emptyList()) }
        LaunchedEffect(homeDatas) {
            snapshotFlow { homeDatas.itemSnapshotList.items }
                .collect { items ->
                    dataList = items.toList()
                }
        }


        CommonPagingPage(
            homeDatas,
            enableRefresh = true,
            lazyListState = lazyListState,
            onScrollStop = { visibleItem, isScrollingUp ->
                ToastUtils.showShort("是否上划${isScrollingUp}  ${visibleItem.toList()}")
            },
            emptyDataContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue)
                )
            },
            loadingContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }, headContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Magenta).click {
                            ToastUtils.showShort("${homeDatas.itemSnapshotList.items}")
                        }
                )
            }, foodContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Yellow)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.title,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


