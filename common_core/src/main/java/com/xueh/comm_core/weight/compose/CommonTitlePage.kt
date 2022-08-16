package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xueh.comm_core.R

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
    content: @Composable ConstraintLayoutScope.() -> Unit,
) {
    Column() {
        CommonTitleView(title, backIcon) {
            blockClick.invoke()
        }
        Divider()
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
        ) {
            content.invoke(this)
        }
    }
}


//公用标题View
@Composable
fun CommonTitleView(
    name: String,
    @DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
    modifier: ((Modifier) -> Unit)? = null,
    blockClick: () -> Unit,
) {
    ProvideWindowInsets {
        rememberSystemUiController().setStatusBarColor(
            Color.Transparent, darkIcons = MaterialTheme.colors.isLight
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .background(Color.White).also {
                    modifier?.invoke(it)
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
                val (iv_close, row_title) = createRefs()
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
                        color = Color.Black,
                    )
                }

                ImageCompose(id = backIcon, modifier = Modifier
                    .size(28.dp)
                    .constrainAs(iv_close) {
                        top.linkTo(row_title.top)
                        bottom.linkTo(row_title.bottom)
                        start.linkTo(parent.start, 16.dp)
                    }
                    .click {
                        blockClick.invoke()
                    })
            }

        }
    }
}
