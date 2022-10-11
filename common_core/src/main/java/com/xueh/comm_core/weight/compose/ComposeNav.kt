package com.xueh.comm_core.weight.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

/**
 * 创 建 人: xueh
 * 创建日期: 2022/10/11
 * 备注：
 */
data class NavData(
    var selectIcon: Int,
    var unSelectIcon: Int,
    var text: String,
    var showRed: Boolean,
)


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
    msgRed: Boolean = false,
    navList: MutableList<NavData> = mutableListOf(
        NavData(selectIcon = R.mipmap.bar_icon_back_white, unSelectIcon = R.mipmap.bar_icon_back_black, text = "消息", showRed = msgRed),
        NavData(selectIcon = R.mipmap.bar_icon_back_white, unSelectIcon = R.mipmap.bar_icon_back_black, text = "首页", showRed = false),
        NavData(selectIcon = R.mipmap.bar_icon_back_white, unSelectIcon = R.mipmap.bar_icon_back_black, text = "我的", showRed = false),
    ),
    itemClick: ((Int) -> Boolean) = { false },
) {
    var selectPos by remember {
        mutableStateOf(1)
    }
    itemClick.invoke(selectPos)
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .height(49.dp), horizontalArrangement = Arrangement.SpaceAround) {
        navList.forEachIndexed { index, navData ->
            NavItem(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .click {
                    var click = itemClick.invoke(index)
                    if (!click) {
                        selectPos = index
                    }
                }, navData, selectPos == index)
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
                    Box(modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.Red))
                }
            }
        }
        Text(text = navData.text, fontSize = 10.sp, color = if (select) selectTextColor else unSelectTextColor)
    }
}