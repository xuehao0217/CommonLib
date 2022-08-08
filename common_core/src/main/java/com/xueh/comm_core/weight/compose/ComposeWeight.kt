package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.CommonLibTheme
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import kotlin.math.roundToInt


@Composable
fun ComposeTitleView(
    title: String,
    backClick: () -> Unit,
    rightView: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.bar_arrows_left_black),
            contentDescription = "返回图标",
            modifier = Modifier
                .clickable {
                    backClick.invoke()
                },
        )
        Text(text = title, fontSize = 18.sp)
        Box {
            rightView?.invoke()
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////


//SpanText(
//list = listOf(
//SpanTextEntity("登录即同意", SpanStyle(color = Color.Black)),
//SpanTextEntity("《用户协议》", SpanStyle(color = Color.Blue)) {
//    ToastUtils.showShort("点击了 用户协议")
//},
//SpanTextEntity("和", SpanStyle(color = Color.Black)),
//SpanTextEntity("《隐私政策》", SpanStyle(color = Color.Blue)) {
//    ToastUtils.showShort("点击了 隐私政策")
//},
//), modifier = Modifier.constrainAs(tv_login_desc) {
//    start.linkTo(iv_avatar.start)
//    end.linkTo(iv_avatar.end)
//    top.linkTo(tv_login_state.bottom)
//}
//)

@Composable
fun SpanText(list: List<SpanTextEntity>, modifier: Modifier = Modifier) {
    val spanText = buildAnnotatedString {
        repeat(list.size) {
            val item = list[it]
            withStyle(item.spanStyle) {
                pushStringAnnotation(item.text,item.text)
                append(item.text)
            }
        }
    }
    ClickableText(text = spanText, modifier = modifier, onClick = {
        repeat(list.size) { index ->
            spanText.getStringAnnotations(tag = list[index].text, start = it, end = it)
                .firstOrNull()
                ?.let { list[index].click?.invoke() }
        }
    })
}

data class SpanTextEntity(var text:String, var spanStyle: SpanStyle, var click:(()->Unit)?=null)

//////////////////////////////////////////////////////////////////////////////////////




@Composable
fun AnimLoading(visible: MutableState<Boolean>, @DrawableRes id: Int, size: Int) {
    AnimatedVisibility(visible = visible.value) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing,
                ),
            )
        )
        val imageBitmap =
            ImageBitmap.imageResource(id = id)
        Canvas(
            modifier = Modifier
                .size(size.dp)
        ) {
            rotate(rotation) {
                drawImage(imageBitmap)
            }
        }
    }
}