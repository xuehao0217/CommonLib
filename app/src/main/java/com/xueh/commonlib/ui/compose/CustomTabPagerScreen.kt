package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.xueh.comm_core.weight.compose.MyTabRow
import com.xueh.comm_core.weight.compose.PagerTab
import com.xueh.comm_core.weight.compose.PagerTabIndicator
import com.xueh.comm_core.weight.compose.click
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Math.*

/**
 * 创 建 人: xueh
 * 创建日期: 2023/3/1
 * 备注：
 */


val pages = arrayOf("Home", "Shows上上 上 ", "Books")

@Composable
fun CustomTabPagerScreen() {
    Scaffold(topBar = {
    }) {
        Column(Modifier.padding(it)) {
            val pagerState = rememberPagerState(pageCount = {pages.size})
            MyTabRow(
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
                            .click {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                            .height(50.dp)
                    )
                }
            }
            HorizontalPager(
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

