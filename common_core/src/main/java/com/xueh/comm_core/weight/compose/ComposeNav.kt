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
import com.lt.compose_views.nav.NavContent
import com.lt.compose_views.nav.PagerNav
import com.lt.compose_views.nav.PagerNavState
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.R
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


@Composable
fun NavPage(
    pages: List<NavContent> = mutableListOf(),
    selectTextColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    unSelectTextColor: Color = Color.Gray,
    textFontSize: Int = 10,
    imageSize: Int = 24,
    interceptClick: () -> Int = { -1},//表示拦截第几个角标 默认-1
    navList: MutableList<NavData> = mutableListOf(),
) {
    val selectPos = rememberMutableStateOf { 0 }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val state = PagerNavState(
            pages
        )
        PagerNav(
            state,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Nav(
            selectPos = selectPos,
            itemList = navList,
            selectTextColor = selectTextColor,
            unSelectTextColor = unSelectTextColor,
            textFontSize = textFontSize,
            imageSize = imageSize
        ) {
           if (interceptClick()==it){
               interceptClick.invoke()
               true
           }else{
               state.nav(state.navContents[it].route)
               false
           }
        }
    }
}

@Preview
@Composable
fun Nav(
    selectPos: MutableState<Int> = mutableIntStateOf(0),
    selectTextColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    unSelectTextColor: Color = Color.Gray,
    textFontSize: Int = 10,
    imageSize: Int = 24,
    itemList: MutableList<NavData> = mutableListOf(),
    itemClick: ((Int) -> Boolean) = { false },// true 拦截 false 不拦截
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(49.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        itemList.forEachIndexed { index, navData ->
            NavItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .click {
                        if (!itemClick.invoke(index)) {
                            selectPos.value = index
                        }
                    },
                navData,
                selectPos.value == index,
                selectTextColor = selectTextColor,
                unSelectTextColor = unSelectTextColor,
                imageSize = imageSize,
                textFontSize = textFontSize,
            )
        }
    }
}

@Preview
@Composable
fun NavItem(
    modifier: Modifier = Modifier,
    navData: NavData = NavData(
        selectIcon = R.mipmap.bar_icon_back_white,
        unSelectIcon = R.mipmap.bar_icon_back_black,
        text = "",
        showRed = false
    ),
    select: Boolean = true,
    selectTextColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    unSelectTextColor: Color = Color.Gray,
    imageSize: Int = 24,
    textFontSize: Int = 10,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            ImageCompose(
                id = if (select) navData.selectIcon else navData.unSelectIcon,
                modifier = Modifier.size(24.dp)
            )
            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.size(imageSize.dp)) {
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
        Text(
            text = navData.text,
            fontSize = textFontSize.sp,
            color = if (select) selectTextColor else unSelectTextColor
        )
    }
}