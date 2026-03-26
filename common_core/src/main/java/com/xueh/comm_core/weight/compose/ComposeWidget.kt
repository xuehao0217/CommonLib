package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//------------------------------------------- Text 相关 -------------------------------------------

/**
 * 可点击的富文本
 *
 * 使用 LinkAnnotation 替代已弃用的 ClickableText，
 * 通过 buildAnnotatedString + withLink 实现每段文本的独立点击事件。
 *
 * @param list 包含文本、样式和点击事件的列表
 */
@Composable
fun SpanText(list: List<SpanTextEntity>, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        list.forEach { item ->
            if (item.click != null) {
                val link = LinkAnnotation.Clickable(
                    tag = item.text,
                    linkInteractionListener = { item.click.invoke() }
                )
                withLink(link) {
                    withStyle(item.spanStyle) {
                        append(item.text)
                    }
                }
            } else {
                withStyle(item.spanStyle) {
                    append(item.text)
                }
            }
        }
    }
    Text(
        text = annotatedString,
        modifier = modifier,
    )
}

data class SpanTextEntity(
    val text: String,
    val spanStyle: SpanStyle,
    val click: (() -> Unit)? = null
)


/**
 * 高亮文本
 */
@Composable
fun HighlightText(
    text: String,
    highlight: String,
    highlightColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        val start = text.indexOf(highlight)
        if (start >= 0) {
            addStyle(SpanStyle(color = highlightColor), start, start + highlight.length)
        }
    }
    Text(text = annotatedString, modifier = modifier, style = style, color = textColor)
}

private const val SPLICING_INLINE_KEY = "splicing_tag"

/**
 * 带内嵌标签的文本
 *
 * @param str 主体文本
 * @param tagText 标签文字
 * @param tagColor 标签背景色
 * @param tagTextColor 标签文字颜色
 * @param tagCornerRadius 标签圆角
 * @param tagWidth 标签宽度
 * @param tagHeight 标签高度
 * @param placeholderWidth InlineContent 占位宽度（需 >= tagWidth + 间距）
 * @param placeholderHeight InlineContent 占位高度
 */
@Composable
fun SplicingText(
    str: String,
    modifier: Modifier = Modifier,
    tagText: String = "Splicing",
    tagColor: Color = Color(0xFFFE3714),
    tagTextColor: Color = Color.White,
    tagCornerRadius: Dp = 3.dp,
    tagWidth: Dp = 35.dp,
    tagHeight: Dp = 18.dp,
    placeholderWidth: TextUnit = 43.sp,
    placeholderHeight: TextUnit = 18.sp,
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        appendInlineContent(SPLICING_INLINE_KEY)
        append(str)
    }
    val inlineContent = mapOf(
        SPLICING_INLINE_KEY to InlineTextContent(
            Placeholder(placeholderWidth, placeholderHeight, PlaceholderVerticalAlign.TextCenter)
        ) {
            BoxText(
                text = tagText,
                textColor = tagTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .width(tagWidth)
                    .height(tagHeight)
                    .background(tagColor, RoundedCornerShape(tagCornerRadius))
            )
            Spacer(Modifier.width(8.dp))
        }
    )
    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = style,
        modifier = modifier
    )
}

//------------------------------------------- Spacer 相关 -------------------------------------------

/** 宽度间距，接受 Int 类型参数（单位 dp） */
@Composable fun SpacerW(dp: Int) = Spacer(Modifier.width(dp.dp))

/** 高度间距，接受 Int 类型参数（单位 dp） */
@Composable fun SpacerH(dp: Int) = Spacer(Modifier.height(dp.dp))

/** 宽度间距，接受 Dp 类型参数 */
@Composable fun SpacerWidth(width: Dp) = Spacer(Modifier.width(width))

/** 高度间距，接受 Dp 类型参数 */
@Composable fun SpacerHeight(height: Dp) = Spacer(Modifier.height(height))

//------------------------------------------- BoxText 相关 -------------------------------------------

/**
 * 居中文本 Box
 */
@Composable
fun BoxText(
    text: String,
    textColor: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text, color = textColor, fontSize = fontSize, fontWeight = fontWeight)
    }
}

/**
 * 居中文本 Box 支持 TextStyle
 */
@Composable
fun BoxText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text, style = style)
    }
}

//------------------------------------------- Loading 相关 -------------------------------------------

/**
 * 转圈 Loading 动画
 */
@Composable
fun AnimLoading(@DrawableRes id: Int, size: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing))
    )
    val imageBitmap = ImageBitmap.imageResource(id = id)
    Canvas(Modifier.size(size.dp)) {
        rotate(rotation) { drawImage(imageBitmap) }
    }
}

//------------------------------------------- 阴影 -------------------------------------------

/**
 * 垂直渐变阴影
 */
@Composable
fun ShadowVerticalView(  colors: List<Color>,height: Int = 50, modifier: Modifier = Modifier) {
    val brush = Brush.verticalGradient(colors)
    Box(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(height.dp).background(brush))
    }
}

//------------------------------------------- TabPage 相关 -------------------------------------------

/**
 * 通用滑动 Tab 页面
 */
@Composable
fun CommonTabPage(
    tabsName: List<String>,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageContent: @Composable (page: Int) -> Unit
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
            divider = {}
        ) {
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = beyondViewportPageCount
        ) { page ->
            pageContent(page)
        }
    }
}

//------------------------------------------- TextField 相关 -------------------------------------------

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
    onValueChange: (String) -> Unit
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
            enabled = textFieldEnabled
        )

        if (showDel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImageCompose(delIconId, Modifier.size(delIconSize).click { delClick() })
                SpacerWidth(delIconEndDP)
            }
        }
    }
}
