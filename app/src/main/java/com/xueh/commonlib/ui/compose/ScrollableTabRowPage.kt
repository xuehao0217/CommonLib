package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/19
 * 备注：
 */

@Preview()
@Composable
fun scrollableTabRow() {
    val tabIndex = remember {
        mutableStateOf(0)
    }
    val tabDatas = ArrayList<String>().apply {
        add("第1tab")
        add("第2tab")
        add("第3tab")
        add("第4tab")
        add("第5tab")
        add("第6tab")
        add("第7tab")
        add("第8tab")
        add("第9tab")
    }

    Column() {
        ScrollableTabRow(
            selectedTabIndex = tabIndex.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            edgePadding = 10.dp,
            divider = {
                TabRowDefaults.Divider(color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            },
            indicator = {
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(it[tabIndex.value]),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    height = 2.dp
                )
            },
            tabs = {
                tabDatas.forEachIndexed { index, s ->
                    leadingIconTabView(index, s, tabIndex)
                }
            })
        Spacer(modifier = Modifier.height(30.dp))
        tabRow()
    }

}


@Composable
fun leadingIconTabView(index: Int, text: String, tabIndex: MutableState<Int>) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPress = interactionSource.collectIsPressedAsState().value
    val imageVector = when (index) {
        0 -> Icons.Filled.Home
        1 -> Icons.Filled.Send
        2 -> Icons.Filled.Favorite
        3 -> Icons.Filled.Search
        4 -> Icons.Filled.Home
        5 -> Icons.Filled.Share
        6 -> Icons.Filled.Favorite
        7 -> Icons.Filled.Favorite
        8 -> Icons.Filled.Add
        else -> Icons.Filled.Person
    }
    LeadingIconTab(
        selected = index == tabIndex.value,
        onClick = {
            tabIndex.value = index
        },
        text = {
            Text(
                text = text,
                color = if (isPress || index == tabIndex.value) androidx.compose.material3.MaterialTheme.colorScheme.primary  else androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        },
        icon = {
            Icon(
                imageVector,
                contentDescription = "icon图标",
                tint = if (isPress || index == tabIndex.value) androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer  else androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
            )
        },
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
        enabled = true,
        interactionSource = interactionSource,
        selectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
        unselectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
    )
}


@Preview()
@Composable
fun tabRow() {
    val tabIndex = remember {
        mutableStateOf(0)
    }
    val tabDatas = ArrayList<String>().apply {
        add("语文")
        add("数学")
        add("英语")
    }
    TabRow(
        selectedTabIndex = tabIndex.value,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        divider = {
            TabRowDefaults.Divider()
        },
        indicator = {
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(it[tabIndex.value]),
                color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                height = 2.dp
            )
        }
    ) {
        tabDatas.forEachIndexed { index, s ->
            tabView(index, s, tabIndex)
        }
    }
}

@Composable
fun tabView(index: Int, text: String, tabIndex: MutableState<Int>) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPress = interactionSource.collectIsPressedAsState().value
    Tab(
        selected = index == tabIndex.value,
        onClick = {
            tabIndex.value = index
        },
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        enabled = true,
        interactionSource = interactionSource,
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Black
    ) {
        Text(
            text = text,
            color = if (isPress || index == tabIndex.value) androidx.compose.material3.MaterialTheme.colorScheme.background else androidx.compose.material3.MaterialTheme.colorScheme.background
        )
    }
}