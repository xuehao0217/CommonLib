package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.xueh.comm_core.weight.compose.PagerTab
import com.xueh.comm_core.weight.compose.PagerTabIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Math.*

/**
 * 创 建 人: xueh
 * 创建日期: 2023/3/1
 * 备注：
 */


val pages = arrayOf("Home", "Shows", "Books")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomiTabPagerScreen() {
    Scaffold(topBar = {
    }) {
        Column(Modifier.padding(it)) {
            val pagerState = rememberPagerState()
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
                },
                backgroundColor = Color.White,
                divider = {}
            ) {
                val scope: CoroutineScope = rememberCoroutineScope()
                pages.forEachIndexed { index, title ->
                    PagerTab(pagerState = pagerState,
                        index = index,
                        pageCount = pages.size,
                        text = title,
                        modifier = Modifier
                            .height(50.dp)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            })
                }
            }
            HorizontalPager(
                count = pages.size,
                state = pagerState,
            ) { page ->
                Text(
                    text = "",
                    Modifier
                        .fillMaxSize()
                        .background(
                            when (page) {
                                0 -> Color.Red
                                1 -> Color.Blue
                                2 -> Color.Yellow
                                else -> {
                                    Color.White
                                }
                            }
                        )
                )
            }
        }
    }
}

