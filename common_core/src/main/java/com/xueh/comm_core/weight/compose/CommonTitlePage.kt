package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.xueh.comm_core.base.compose.theme.appThemeState

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/15
 * 备注：
 */

//公用带标题页面
@Composable
fun CommonTitlePage(
    title: String,
    @DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
    blockClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column() {
        CommonTitleView(title, backIcon) {
            blockClick.invoke()
        }
        Divider()
        Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()) {
            content.invoke()
        }
    }
}


@Composable
fun CommonTitleView(
    name: String,
    @DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
    titleRightView: (@Composable () -> Unit)? = null,
    blockClick: () -> Unit,
) {
    ProvideWindowInsets {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background).also {

                }
        ) {
            Spacer(
                modifier = Modifier
                    .statusBarsHeight()
                    .fillMaxWidth()
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                val (iv_close, row_title, v_right) = createRefs()
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
                        color = if (appThemeState.value.darkTheme) Color.White else Color.Black,
                    )
                }

                ImageCompose(id = if (appThemeState.value.darkTheme) R.mipmap.bar_icon_back_white else R.mipmap.bar_icon_back_black,
                    modifier = Modifier
                        .size(28.dp)
                        .constrainAs(iv_close) {
                            top.linkTo(row_title.top)
                            bottom.linkTo(row_title.bottom)
                            start.linkTo(parent.start, 16.dp)
                        }
                        .click {
                            blockClick.invoke()
                        })

                Surface(modifier = Modifier.constrainAs(v_right) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, 15.dp)
                }) {
                    titleRightView?.invoke()
                }

            }

        }
    }
}
