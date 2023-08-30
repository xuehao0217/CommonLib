package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.compose.rememberMutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2022/10/11
 * 备注：
 */
data class NavData(
    var selectIcon: Int,
    var unSelectIcon: Int,
    var text: String,
    var showRed: Boolean = false,
)


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun NavPage(
    interceptIndex: Int = -1,
    interceptClick: () -> Unit = {},
    navList: MutableList<NavData> = mutableListOf(),
    pageContent: @Composable PagerScope.(page: Int) -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { navList.size })
    val scope: CoroutineScope = rememberCoroutineScope()
    var selectPos by rememberMutableStateOf(value = 0)
    selectPos = pagerState.currentPage
    Column {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            pageContent(page)
        }
        Nav(selectPos = selectPos, itemList = navList) {
            scope.launch {
                if (it != interceptIndex) {
                    pagerState.scrollToPage(page = it)
                } else {
                    interceptClick.invoke()
                }
            }
        }
    }
}

//Nav(mViewModel.msgRedShow) {
//    if (it == 0) {
//        拦截
//        true
//    } else {
//        不拦截
//        false
//    }
//}
@Preview
@Composable
fun Nav(
    selectPos: Int = 0,
    itemList: MutableList<NavData> = mutableListOf(),
    itemClick: (Int) -> Unit = {},
) {
    itemClick.invoke(selectPos)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(49.dp), horizontalArrangement = Arrangement.SpaceAround
    ) {
        itemList.forEachIndexed { index, navData ->
            NavItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .click {
                        itemClick.invoke(index)
                    }, navData, selectPos == index
            )
        }
    }
}

@Preview
@Composable
fun NavItem(
    modifier: Modifier = Modifier,
    navData: NavData = NavData(selectIcon = R.mipmap.bar_icon_back_white, unSelectIcon = R.mipmap.bar_icon_back_black, text = "星辰", showRed = false),
    select: Boolean = true,
    selectTextColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    unSelectTextColor: Color = Color.Gray,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(contentAlignment = Alignment.Center) {
            ImageCompose(id = if (select) navData.selectIcon else navData.unSelectIcon, modifier = Modifier.size(24.dp))
            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.size(26.dp)) {
                if (navData.showRed) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
        }
        Text(text = navData.text, fontSize = 10.sp, color = if (select) selectTextColor else unSelectTextColor)
    }
}