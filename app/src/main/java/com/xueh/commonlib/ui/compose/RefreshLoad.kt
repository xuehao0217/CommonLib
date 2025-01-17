package com.xueh.commonlib.ui.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.weight.compose.PagingLazyColumn
import com.xueh.comm_core.weight.compose.PagingRefresh
import com.xueh.comm_core.weight.compose.PagingStateAppend
import com.xueh.comm_core.weight.compose.PagingStateRefresh
import com.xueh.comm_core.weight.compose.click
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel

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
        var alpha by rememberMutableStateOf { 0f }
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
        homeDatas.PagingRefresh {
            homeDatas.PagingLazyColumn(pagingRefreshStateContent = {
                homeDatas.PagingStateRefresh(stateEmpty = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue)
                    )
                }, stateError = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                    )
                })
            }, pagingAppendStateContent = {
                homeDatas.PagingStateAppend(stateError = {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Red)
                        .click {
                            homeDatas.retry()
                        })
                }, stateNoMore = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Green)
                    )
                })
            }) {
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
}


