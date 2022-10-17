package com.xueh.comm_core.weight.compose

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.BaseComposeView
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.utils.compose.setSystemBarsColor
import com.xueh.comm_core.utils.compose.transparentStatusBar

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/15
 * 备注：
 */

//公用带标题页面
@Composable
fun CommonTitlePage(
    activity: Activity,
    title: String,
    showBackIcon: Boolean = true,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    backClick: (() -> Unit)? = null,
    showTitleBottomLine: Boolean = true,
    titleBackground: Color? = null,
    contentBackground: Color? = null,
    titleRightContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    activity.transparentStatusBar()
    activity.setSystemBarsColor(color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
        darkIcons = appThemeState.darkTheme)
    BaseComposeView {
        GrayAppAdapter {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(contentBackground ?: androidx.compose.material3.MaterialTheme.colorScheme.background)
            ) {
                if (title.isNotEmpty()) {
                    CommonTitleView(title,
                        showBackIcon = showBackIcon,
                        backIcon = backIcon,
                        rightContent = titleRightContent,
                        titleBackground = titleBackground ?: androidx.compose.material3.MaterialTheme.colorScheme.background) {
                        activity.finish()
                    }
                    if (showTitleBottomLine) {
                        androidx.compose.material3.Divider(color = Color.Gray, thickness = 0.5.dp)
                    }
                }
                Surface(color = contentBackground ?: androidx.compose.material3.MaterialTheme.colorScheme.background, modifier = Modifier
                    .fillMaxSize()) {
                    content.invoke()
                }
            }
        }

    }
}


@Composable
fun CommonTitleView(
    name: String,
    @DrawableRes backIcon: Int = if (appThemeState.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
    titleBackground: Color = androidx.compose.material3.MaterialTheme.colorScheme.background,
    showBackIcon: Boolean = true,
    rightContent: (@Composable () -> Unit)? = null,
    backClick: (() -> Unit)? = null,
) {
    ProvideWindowInsets {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .background(titleBackground)
        ) {
            Spacer(
                modifier = Modifier
                    .statusBarsHeight()
                    .fillMaxWidth()
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(titleBackground)
                    .height(44.dp)
            ) {
                val (iv_close, row_title, surface_right_view) = createRefs()
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.constrainAs(row_title) {
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
                androidx.compose.material.Surface(modifier = Modifier
                    .background(titleBackground)
                    .constrainAs(surface_right_view) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, 16.dp)
                    }) {
                    rightContent?.invoke()
                }
            }

        }
    }
}
