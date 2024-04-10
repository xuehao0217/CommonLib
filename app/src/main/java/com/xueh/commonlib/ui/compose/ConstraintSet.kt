package com.xueh.commonlib.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.xueh.commonlib.R

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/18
 * 备注：
 */
@Preview(showBackground = true)
@Preview
@Composable
fun constraint() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
    ) {
        val (tv) = createRefs()
        Text(
            modifier = Modifier
                .background(Color.Blue)
                .constrainAs(tv) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                }, text = "2222222"
        )
    }

}


@Composable
fun ConstraintPage() {
    val orientation = remember { mutableStateOf(1) }
    ConstraintLayout(
        getConstraintLayout(orientation),
        Modifier
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .padding(12.dp, 12.dp, 12.dp, 12.dp), animateChanges = true

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "效果图片",
            modifier = Modifier
                .layoutId("imageRef")
                .fillMaxWidth()
                .clickable {
                    if (orientation.value == 0) {
                        orientation.value = 1
                    } else {
                        orientation.value = 0
                    }
                }
                .clip(shape = RoundedCornerShape(5)),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = "泰迪犬其实是贵宾犬的一种。根据体型大小被分为四类，最受欢迎的是体型较小的品种：迷你贵宾犬和玩具贵宾犬。其中玩具贵宾犬是体型最小的一种，个性好动、欢快、非常机警、聪明、喜欢外出、性格脾气好、适应力强。贵宾犬不脱毛，是极好的宠物犬。如果红色玩具贵宾犬不剃胡须和嘴边的毛可以长成动漫画里面泰迪熊的模样，所以红色（褐色）玩具贵宾犬又叫“泰迪",
            modifier = Modifier.layoutId("titleRef"),
            fontSize = 18.sp,
            textAlign = TextAlign.Left,
            overflow = TextOverflow.Ellipsis,
            maxLines = if (orientation.value == 0) Int.MAX_VALUE else 4,
        )
    }
}


private fun getConstraintLayout(orientation: MutableState<Int>): androidx.constraintlayout.compose.ConstraintSet {
    return androidx.constraintlayout.compose.ConstraintSet {
        val imageRef = createRefFor("imageRef")
        val titleRef = createRefFor("titleRef")

        if (orientation.value == 0) {
            constrain(imageRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }
            constrain(titleRef) {
                start.linkTo(imageRef.start)
                end.linkTo(imageRef.end)
                top.linkTo(imageRef.bottom, 16.dp)
                width = Dimension.fillToConstraints
            }
        } else {
            constrain(imageRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                width = Dimension.value(100.dp)
                height = Dimension.value(100.dp)
            }
            constrain(titleRef) {
                start.linkTo(imageRef.end, 8.dp)
                top.linkTo(imageRef.top, 2.dp)
                end.linkTo(parent.end)
                bottom.linkTo(imageRef.bottom, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        }
    }
}