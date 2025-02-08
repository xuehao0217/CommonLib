package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xueh.comm_core.helper.isEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//加载图片
// https://coil-kt.github.io/coil/compose/
// https://github.com/coil-kt/coil/blob/main/README-zh.md
@Composable
fun ImageLoadAsyncImage(
    url: String, modifier: Modifier = Modifier, placeholder: Painter? = null,
    error: Painter? = null, onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) = AsyncImage(
    model = ImageRequest.Builder(LocalContext.current).data(url.trim()).crossfade(true).build(),
    modifier = modifier,
    contentScale = ContentScale.Crop,
    contentDescription = null,
    placeholder = placeholder, //加载中展位图
    error = error,
    onError = onError,
    onSuccess = onSuccess //加载成功
)


@Composable
fun ImageLoadCompose(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url.trim()).crossfade(true).build(),
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = null,
        loading = loading,
        success = success,
        error = error,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
    )
}


//@Composable
//fun ImageCompose(
//    @DrawableRes id: Int,
//    modifier: Modifier = Modifier,
//    colorFilter: ColorFilter? = null,
//    alignment: Alignment = Alignment.Center,
//    contentScale: ContentScale = ContentScale.Crop,
//) {
//    val imgLoader = ImageLoader.Builder(Utils.getApp())
//        .components {
//            if (Build.VERSION.SDK_INT >= 28) {
//                add(ImageDecoderDecoder.Factory())
//            } else {
//                add(GifDecoder.Factory())
//            }
//        }
//        .build()
//
//    Image(
//        painter = rememberAsyncImagePainter(id, imgLoader),
//        contentDescription = "ImageComposeContentDescription",
//        modifier = modifier,
//        contentScale = contentScale,
//        colorFilter = colorFilter,
//        alignment = alignment
//    )
//}

@Composable
fun ImageCompose(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
) = Image(
    painter = painterResource(id = id),
    contentDescription = "ImageComposeContentDescription",
    modifier = modifier,
    contentScale = contentScale,
    colorFilter = colorFilter,
    alignment = alignment
)
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
                pushStringAnnotation(item.text, item.text)
                append(item.text)
            }
        }
    }
    ClickableText(text = spanText, modifier = modifier, onClick = {
        repeat(list.size) { index ->
            spanText.getStringAnnotations(tag = list[index].text, start = it, end = it)
                .firstOrNull()?.let { list[index].click?.invoke() }
        }
    })
}

data class SpanTextEntity(
    var text: String,
    var spanStyle: SpanStyle,
    var click: (() -> Unit)? = null,
)

//////////////////////////////////////////////////////////////////////////////////////


@Composable
fun SpacerW(int: Int) {
    Spacer(Modifier.width(int.dp))
}


@Composable
fun SpacerH(int: Int) {
    Spacer(Modifier.height(int.dp))
}

@Composable
fun SpacerWidth(int: Dp) {
    Spacer(Modifier.width(int))
}


@Composable
fun SpacerHeight(int: Dp) {
    Spacer(Modifier.height(int))
}


//转圈loading
@Composable
fun AnimLoading(@DrawableRes id: Int, size: Int) {
    Box(contentAlignment = Alignment.Center) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing,
                ),
            ), label = ""
        )
        val imageBitmap = ImageBitmap.imageResource(id = id)
        Canvas(
            modifier = Modifier.size(size.dp)
        ) {
            rotate(rotation) {
                drawImage(imageBitmap)
            }
        }
    }
}


@Composable
fun BoxText(
    text: String,
    textColor: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text(
            text = text, color = textColor, fontSize = fontSize, fontWeight = fontWeight
        )
    }
}


@Composable
fun BoxText(
    text: String,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text(
            text = text, style = style
        )
    }
}


//阴影垂直
@Composable
fun ShadowVerticalView(height: Int = 50, modifier: Modifier = Modifier) {
    val colorList = arrayListOf(Color(0x00F5F7F8), Color(0xFFF5F7F8))
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .background(
                    brush = Brush.verticalGradient(colorList),
                )
        )
    }
}


//公用带滑动Tab页面
@Composable
fun CommonTabPage(beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount, tabsName: List<String>, pageContent: @Composable (page: Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = {
            tabsName.size
        })
        MyTabRow(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 12.dp),
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
            },
            backgroundColor = Color.Transparent,
            divider = {}) {
            val scope: CoroutineScope = rememberCoroutineScope()
            tabsName.forEachIndexed { index, title ->
                PagerTab(pagerState = pagerState,
                    index = index,
                    pageCount = tabsName.size,
                    text = title,
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .click {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .height(22.dp))
            }
        }

        HorizontalPager(state = pagerState, Modifier.weight(1f),beyondViewportPageCount=beyondViewportPageCount) { page ->
            if (page == pagerState.currentPage) {
//                pageContent(page, page == pagerState.currentPage)
                pageContent(page)
            }
        }
    }
}


/*
var textFieldEnabled by remember {
    mutableStateOf(true)
}
var input by remember {
    mutableStateOf("")
}

MyTextField(
text = input,
delClick = {
    input = ""
},
onValueChange = {
    input = it
},
keyboardOptions = KeyboardOptions(
keyboardType = KeyboardType.Text,
  imeAction = ImeAction.Search,//键盘右下角
),
maxLength = 7,
textFieldPadding = 0,
textFieldEnabled = textFieldEnabled,
delIconId = R.mipmap.ic_search_close,
delIconSize = 12.dp,
keyboardActions = KeyboardActions(
                        onSearch = {

                        },
                    ),
delIconEndDP = 12, modifier = Modifier
.height(32.dp)
.weight(1f)
)
*/

@Composable
fun MyTextField(
    text: String = "",
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current,
    hintText: String = "",
    hintStyle: TextStyle = androidx.compose.material3.LocalTextStyle.current,
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
    var showDel by remember {
        mutableStateOf(text.isNotEmpty())
    }
    Box(
        modifier = modifier, contentAlignment = Alignment.CenterEnd
    ) {
        BasicTextField(
            value = text,
            textStyle = style,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange.invoke(it)
                }
                showDel = it.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = textFieldPadding),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = 1,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.isEmpty()) {
                        Text(text = hintText, style=hintStyle)
                    }
                    innerTextField()
                }
            },
            enabled = textFieldEnabled,
        )
        if (showDel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImageCompose(id = delIconId, modifier = Modifier
                    .size(delIconSize)
                    .click {
                        delClick.invoke()
                    })
                SpacerWidth(int = delIconEndDP)
            }
        }
    }
}


@Composable
fun HighlightText(
    text: String,
    textColor: Color = Color.Unspecified,
    highlight: String,
    highlightColor: Color = Color.Unspecified,
    modifier: Modifier,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = androidx.compose.material3.LocalTextStyle.current.toSpanStyle()) {
            append(text)
        }
        val startIndexOfHighlight = text.indexOf(highlight)
        if (startIndexOfHighlight >= 0) {
            addStyle(
                style = SpanStyle(color = highlightColor),
                start = startIndexOfHighlight,
                end = startIndexOfHighlight + highlight.length
            )
        }
    }
    androidx.compose.material3.Text(
        text = annotatedString,
        modifier = modifier,
        color = textColor,
        style = style
    )
}


@Preview
@Composable
fun SplicingText(
    str: String = "",
    modifier: Modifier = Modifier,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        appendInlineContent(id = "Splicing")
        append(str)
    }
    val inlineContentMap = mapOf(
        "Splicing" to InlineTextContent(
            Placeholder(43.sp, 18.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            Row {
                BoxText(
                    text = "Splicing",
                    textColor = Color(0xFFFFFFFF),
                    fontSize = 10.sp,
                    fontWeight = FontWeight(500),
                    modifier = Modifier
                        .width(35.dp)
                        .height(18.dp)
                        .background(
                            color = Color(0xFFFE3714),
                            shape = RoundedCornerShape(size = 3.dp)
                        )
                )
                SpacerW(int = 8)
            }
        },
    )
    androidx.compose.material3.Text(
        annotatedString,
        inlineContent = inlineContentMap,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = style,
        modifier = modifier,
    )
}
