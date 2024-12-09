package com.xueh.comm_core.weight.compose

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xueh.comm_core.base.compose.theme.ComposeMaterial3Theme

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/15
 * 备注：
 */

//公用带标题页面
@Composable
fun CommonTitlePage(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    backClick: (() -> Unit)? = null,
    showTitleBottomLine: Boolean = true,
    titleBackgroundColor: Color? = null,
    contentBackgroundColor: Color? = null,
    titleRightContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var isSystemInDark = isSystemInDarkTheme()
    LaunchedEffect(isSystemInDark) {
        appThemeState = appThemeState.copy(darkTheme = isSystemInDark)
    }
    rememberSystemUiController().setSystemBarsColor(
        if (appThemeState.darkTheme) Color.Black else Color.Transparent,
        darkIcons = !appThemeState.darkTheme
    )
    ComposeMaterial3Theme {
        GrayAppAdapter(isGray = false) {
            Scaffold(
                modifier = Modifier
                    .systemBarsPadding()
                    .then(modifier),
            ) {
                Column(
                    modifier = Modifier
                        .background(contentBackgroundColor ?: MaterialTheme.colorScheme.background)
                        .padding(it)
                ) {
                    CommonTitleView(
                        title,
                        showBackIcon = showBackIcon,
                        modifier = Modifier.background(titleBackgroundColor
                            ?: MaterialTheme.colorScheme.background),
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
                        modifier = Modifier.fillMaxSize(),
                        color = contentBackgroundColor ?: MaterialTheme.colorScheme.background
                    ) {
                        content.invoke()
                    }
                }
            }
        }
    }
}


@Composable
fun CommonTitleView(
    title: String,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    modifier: Modifier=Modifier,
    titleBackgroundColor: Color = Color.White,
    showBackIcon: Boolean = true,
    rightContent: (@Composable () -> Unit)? = null,
    backClick: (() -> Unit)? = null,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(titleBackgroundColor)
            .height(44.dp).then(modifier)
    ) {
        val (iv_close, row_title, surface_right_view) = createRefs()
        Text(
            text = "${title}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = if (appThemeState.darkTheme) Color.White else Color.Black,
            modifier = Modifier.constrainAs(row_title) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
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
        Box(modifier = Modifier.constrainAs(surface_right_view) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, 16.dp)
        }) {
            rightContent?.invoke()
        }
    }
}
