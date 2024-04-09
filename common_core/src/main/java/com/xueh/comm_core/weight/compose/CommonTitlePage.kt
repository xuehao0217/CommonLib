package com.xueh.comm_core.weight.compose

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeState
import androidx.compose.material3.MaterialTheme
import com.xueh.comm_core.base.compose.theme.BaseComposeTheme

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/15
 * 备注：
 */

//公用带标题页面
@Composable
fun CommonTitlePage(
    title: String,
    showBackIcon: Boolean = true,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    backClick: (() -> Unit)? = null,
    showTitleBottomLine: Boolean = true,
    titleBackgroundColor: Color? = null,
    contentBackgroundColor: Color? = null,
    titleRightContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var  isSystemInDark=isSystemInDarkTheme()
    LaunchedEffect(isSystemInDark) {
        appThemeState = appThemeState.copy(darkTheme = isSystemInDark)
    }

    BaseComposeTheme(systemBarsColor =if (appThemeState.darkTheme) Color.Black else  Color.Transparent, darkTheme = appThemeState.darkTheme) {
        GrayAppAdapter(isGray = false){
            Scaffold(modifier = Modifier.systemBarsPadding(),
//                topBar = {
//                Spacer(
//                    modifier = Modifier
//                        //具体状态栏的颜色在这
//                        .background(titleBackgroundColor ?: MaterialTheme.colorScheme.background)
//                        .statusBarsHeight()
//                        .fillMaxWidth()
//                )
//            }, bottomBar = {
//                Spacer(
//                    modifier = Modifier
//                        .navigationBarsHeight()
//                        .fillMaxWidth()
//                )
//            }
            ) {
                Column(
                    modifier = Modifier
                        .background(contentBackgroundColor ?: MaterialTheme.colorScheme.background)
                        .padding(it)
                ) {
                    CommonTitleView(
                        title,
                        showBackIcon = showBackIcon,
                        titleBackgroundColor = titleBackgroundColor ?: MaterialTheme.colorScheme.background,
                        backIcon = backIcon,
                        rightContent = titleRightContent,
                        backClick = backClick
                    )
                    if (showTitleBottomLine) {
                        Divider(
                            color = if (appThemeState.darkTheme) Color.White else androidx.compose.material.MaterialTheme.colors.onSurface.copy(
                                alpha = 0.12f
                            )
                        )
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(), color = contentBackgroundColor ?: MaterialTheme.colorScheme.background
                    ) {
                        content.invoke()
                    }
                }
            }
        }
    }

//    BaseComposeView {
//        GrayAppAdapter {
//            Scaffold(topBar = {
//                Spacer(
//                    modifier = Modifier
//                        .height(
//                            BarUtils
//                                .getStatusBarHeight().toFloat().dp().dp
//                        )
//                        .background(titleBackground)
//                        .fillMaxWidth()
//                )
//            }, bottomBar = {
//                Spacer(
//                    modifier = Modifier
//                        .height(
//                                BarUtils
//                                    .getNavBarHeight()
//                                    .toFloat().dp().dp
//                        )
//                        .background(contentBackground)
//                        .fillMaxWidth()
//                )
//            }) {
//                Column(
//                    modifier = Modifier
//                        .background(contentBackground)
//                        .padding(it)
//                ) {
//                    CommonTitleView(
//                        title,
//                        showBackIcon = showBackIcon,
//                        titleBackground = titleBackground,
//                        backIcon = backIcon,
//                        rightContent = titleRightContent,
//                        backClick = backClick
//                    )
//                    if (showTitleBottomLine) {
//                        Divider()
//                    }
//                    androidx.compose.material.Surface(
//                        modifier = Modifier
//                            .fillMaxSize(), color = contentBackground
//                    ) {
//                        content.invoke()
//                    }
//                }
//            }
//        }
//    }
}


@Composable
fun CommonTitleView(
    name: String,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    titleBackgroundColor: Color? = null,
    showBackIcon: Boolean = true,
    rightContent: (@Composable () -> Unit)? = null,
    backClick: (() -> Unit)? = null,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(titleBackgroundColor ?: MaterialTheme.colorScheme.background)
            .height(44.dp)
    ) {
        val (iv_close, row_title, surface_right_view) = createRefs()
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.constrainAs(row_title) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            Text(
                text = "${name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (appThemeState.darkTheme) Color.White else Color.Black,
            )
        }
        if (showBackIcon) {
            ImageCompose(id = backIcon, modifier = Modifier
                .size(28.dp)
                .constrainAs(iv_close) {
                    top.linkTo(row_title.top)
                    bottom.linkTo(row_title.bottom)
                    start.linkTo(parent.start, 16.dp)
                }
                .click {
                    backClick?.invoke()
                })

        }
        Surface(modifier = Modifier
            .background(titleBackgroundColor ?: MaterialTheme.colorScheme.background)
            .constrainAs(surface_right_view) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 16.dp)
            }) {
            rightContent?.invoke()
        }
    }
}
