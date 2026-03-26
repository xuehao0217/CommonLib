/** 富文本与可点击片段：[buildAnnotatedString]、[LinkAnnotation]、行内占位等组合件。 */
package com.xueh.comm_core.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 可点击富文本；链接 [LinkAnnotation] 的 tag 使用索引前缀，避免同文案冲突。
 */
@Composable
fun SpanText(list: List<SpanTextEntity>, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        list.forEachIndexed { index, item ->
            if (item.click != null) {
                val link = LinkAnnotation.Clickable(
                    tag = "span_link_$index",
                    linkInteractionListener = { item.click.invoke() },
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
    val click: (() -> Unit)? = null,
)

/** [highlight] 的所有出现位置均应用 [highlightColor]（[highlight] 为空则不做高亮）。 */
@Composable
fun HighlightText(
    text: String,
    highlight: String,
    highlightColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        if (highlight.isNotEmpty()) {
            var searchStart = 0
            while (searchStart <= text.length - highlight.length) {
                val idx = text.indexOf(highlight, searchStart)
                if (idx < 0) break
                addStyle(SpanStyle(color = highlightColor), idx, idx + highlight.length)
                searchStart = idx + highlight.length
            }
        }
    }
    Text(text = annotatedString, modifier = modifier, style = style, color = textColor)
}

private const val SPLICING_INLINE_KEY = "splicing_tag"

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
    style: TextStyle = LocalTextStyle.current,
) {
    val annotatedString = buildAnnotatedString {
        appendInlineContent(SPLICING_INLINE_KEY)
        append(str)
    }
    val inlineContent = mapOf(
        SPLICING_INLINE_KEY to InlineTextContent(
            Placeholder(placeholderWidth, placeholderHeight, PlaceholderVerticalAlign.TextCenter),
        ) {
            BoxText(
                text = tagText,
                textColor = tagTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .width(tagWidth)
                    .height(tagHeight)
                    .background(tagColor, RoundedCornerShape(tagCornerRadius)),
            )
            Spacer(Modifier.width(8.dp))
        },
    )
    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = style,
        modifier = modifier,
    )
}
