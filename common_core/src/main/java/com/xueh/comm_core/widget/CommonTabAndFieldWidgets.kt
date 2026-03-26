package com.xueh.comm_core.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CommonTabPage(
    tabsName: List<String>,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageContent: @Composable (page: Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { tabsName.size })
        val scope = rememberCoroutineScope()

        MyTabRow(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp),
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions -> PagerTabIndicator(tabPositions, pagerState) },
            backgroundColor = Color.Transparent,
            divider = {},
        ) {
            tabsName.forEachIndexed { index, title ->
                PagerTab(
                    pagerState = pagerState,
                    index = index,
                    pageCount = tabsName.size,
                    text = title,
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .clickNoRipple {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .height(22.dp),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = beyondViewportPageCount,
        ) { page ->
            pageContent(page)
        }
    }
}

@Composable
fun MyTextField(
    text: String = "",
    style: TextStyle = LocalTextStyle.current,
    hintText: String = "",
    hintStyle: TextStyle = LocalTextStyle.current,
    @DrawableRes delIconId: Int,
    delIconEndDP: Dp = 8.dp,
    delIconSize: Dp = 8.dp,
    modifier: Modifier = Modifier,
    textFieldPadding: Dp = 18.dp,
    textFieldEnabled: Boolean = true,
    maxLength: Int = 12,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    delClick: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    var showDel by remember { mutableStateOf(text.isNotEmpty()) }
    Box(modifier, contentAlignment = Alignment.CenterEnd) {
        BasicTextField(
            value = text,
            textStyle = style,
            onValueChange = {
                if (it.length <= maxLength) onValueChange(it)
                showDel = it.isNotEmpty()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = textFieldPadding),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = 1,
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (text.isEmpty()) Text(hintText, style = hintStyle)
                    inner()
                }
            },
            enabled = textFieldEnabled,
        )

        if (showDel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImageCompose(
                    delIconId,
                    Modifier.size(delIconSize).clickNoRipple { delClick() },
                )
                SpacerWidth(delIconEndDP)
            }
        }
    }
}
