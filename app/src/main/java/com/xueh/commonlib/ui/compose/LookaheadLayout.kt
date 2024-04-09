package com.xueh.commonlib.ui.compose
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.EditCommand
import androidx.compose.ui.text.input.EditingBuffer
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import com.xueh.comm_core.base.compose.theme.BaseComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun OtherNewPreview() {
    BaseComposeTheme  {
        Column() {
//            TextBrushAlpha()
//            SpanStyleBrushSample()
//            TextAccessibilityDemo()
//            DetectTapPressureGesturesDemo()
//            PullRefreshTest()
//            SnapFlingBehaviorTest()
            Column(modifier = Modifier.fillMaxHeight(0.5f)) {
                Text(text = "LazyVerticalStaggeredGridTest")
                LazyVerticalStaggeredGridTest()
            }
            Column(modifier = Modifier.fillMaxHeight(0.5f)) {
                Text(text = "LazyHorizontalStaggeredGridTest")
                LazyHorizontalStaggeredGridTest()
            }
        }
    }

}


//@Preview(showBackground = true)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlacementScopePreview() {
    BaseComposeTheme  {
        FirstItemHalf {
            Box(modifier = Modifier.background(Color.Red))
            Box(modifier = Modifier.background(Color.Yellow))
            Box(modifier = Modifier.background(Color.Black))
            Box(modifier = Modifier.background(Color.Blue))
        }
    }
}

/**
 *LazyVerticalStaggeredGridView和 LazyHorizontalStaggeredGrid
 * Copy from https://github.com/anandwana001
 **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyHorizontalStaggeredGridTest() {
    LazyHorizontalStaggeredGrid(modifier = Modifier
        .padding(top = 8.dp, bottom = 16.dp),
        rows = StaggeredGridCells.Fixed(5), content = {
            items(listOfItems) {
                SingleCard(
                    modifier = Modifier,
                    text = it
                )
            }
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyVerticalStaggeredGridTest() {
    LazyVerticalStaggeredGrid(modifier = Modifier
        .padding(top = 8.dp, bottom = 16.dp),
        columns = StaggeredGridCells.Fixed(3),
        state = rememberLazyStaggeredGridState(),
        content = {
            items(listOfItems) {
                SingleCard(text = it)
            }
        })
}

@Composable
fun SingleCard(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Text(
            modifier = modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
            text = text,
            color = Color.Green
        )
    }
}

/**
 *SnapFlingBehavior 吸附
 **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SnapFlingBehaviorTest() {
    val state = rememberLazyListState()

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        state = state,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    ) {
        items(200) {
            Box(
                modifier = Modifier
                    .height(400.dp)
                    .width(200.dp)
                    .padding(8.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(it.toString(), fontSize = 32.sp)
            }
        }
    }
}

/**
 *pullRefresh
 *与 PullRefreshState 结合使用的 PullRefresh 修饰符。提供到嵌套滚动系统的连接。
 * 基于 Android 的 SwipeRefreshLayout。
 **/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshTest() {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(Modifier.pullRefresh(state)) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!refreshing) {
                items(itemCount) {
                    ListItem { Text(text = "Item ${itemCount - it}") }
                }
            }
        }

        PullRefreshIndicator(
            refreshing,
            state,
            backgroundColor = Color.Yellow,
            contentColor = Color.Blue,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

///////////////////////////////////////////////////////////////////////////
//Version 1.3.0-alpha03
///////////////////////////////////////////////////////////////////////////
/**
 *添加了实验性 API 以获取 PlacementScope 中的 LayoutCoordinates。这让开发人员知道当前布局相对于其位置放置子级的位置
 **/
//如果可能，布局使第一项占用屏幕宽度的一半。其余布局水平放置在剩余空间中。
@ExperimentalComposeUiApi
@Composable
fun FirstItemHalf(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val view = LocalView.current

    Layout(content = content, modifier = modifier, measurePolicy = { measurables, constraints ->
        var width = constraints.minWidth
        var height = constraints.minHeight
        // 如果这没有固定大小，就是水平布局
        var placeables: List<Placeable>? = null
        if (measurables.isNotEmpty()) {
            if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
                width = constraints.maxWidth
                height = constraints.maxHeight
            } else {
                placeables = measurables.map { it.measure(constraints) }
                width = placeables.sumOf { it.width }
                height = placeables.maxOf { it.height }
            }
        }
        layout(width, height) {
            if (placeables != null) {
                var x = 0
                placeables.forEach {
                    it.placeRelative(x, 0)
                    x += it.width
                }
            } else if (measurables.isNotEmpty() && coordinates != null) {
                val coordinates = coordinates!!
                val positionInWindow = IntArray(2)
                view.getLocationOnScreen(positionInWindow)
                val topLeft = coordinates.localToRoot(Offset.Zero).round() +
                        IntOffset(positionInWindow[0], positionInWindow[1])
                val displayWidth = view.resources.displayMetrics.widthPixels
                val halfWay = displayWidth / 2

                val c0 = if (topLeft.x < halfWay) {
                    // 第一个可测量的应该适合一半
                    Constraints.fixed(
                        halfWay - topLeft.x,
                        height
                    )
                } else {
                    // 第一个已经过了一半，所以把它平分
                    val measureWidth = width / measurables.size
                    Constraints.fixed(measureWidth, height)
                }
                val p0 = measurables[0].measure(c0)
                p0.place(0, 0)

                // 其余的正好适合剩余的空间
                var x = p0.width
                for (i in 1..measurables.lastIndex) {
                    val measureWidth = (width - x) / (measurables.size - i)
                    val p = measurables[i].measure(Constraints.fixed(measureWidth, height))
                    p.place(x, 0)
                    x += p.width
                }
            }
        }
    })
}
///////////////////////////////////////////////////////////////////////////
// Version 1.3.0-alpha02
///////////////////////////////////////////////////////////////////////////


/**
 *Version 1.3.0-alpha02 Rect、RoundRect 和 MutableRect 现在在调用 contains 函数的语法中支持 Kotlin。
 **/

private fun rectContains() {

    val rect = Rect(100f, 10f, 200f, 300f)
    val offset = Offset(177f, 288f)
    rect.contains(offset)  //old
    offset in rect//new

}

/**
 *Version 1.3.0-alpha02 添加了一个新属性 PointerInputChange#pressure 来检索压力。
 **/

@ExperimentalComposeUiApi
@Composable
fun DetectTapPressureGesturesDemo() {

    var pressure by remember { mutableStateOf("Press for value") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("The box displays pressure. Tap or use stylus to see different pressure values.")
        Box(
            Modifier
                .fillMaxSize()
                .padding(20.dp)
                .wrapContentSize(Alignment.Center)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            event.changes.forEach {
                                pressure = "${it.pressure}"
                                it.consume()
                            }
                        }
                    }
                }
                .clipToBounds()//将内容剪辑到此修改器定义的图层的边界
                .background(Color.Green)
                .border(BorderStroke(2.dp, Color.Black))
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                text = "Pressure: $pressure"
            )
        }
    }
}
///////////////////////////////////////////////////////////////////////////
// Version 1.3.0-alpha01
///////////////////////////////////////////////////////////////////////////

/**
 * Version 1.3.0-alpha01新添加
 *引入了 UrlAnnotation 注释类型和关联方法以支持 AnnotatedStrings 中的 TalkBack 链接支持
 **/
@OptIn(ExperimentalTextApi::class)
@Composable
fun TextAccessibilityDemo() {

    Column {
//        TagLine("Text to speech with different locales.")
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(localeList = LocaleList("en-us")))
                append("Hello!\n")
                pop()
                pushStyle(SpanStyle(localeList = LocaleList("en-gb")))
                append("Hello!\n")
                pop()
                pushStyle(SpanStyle(localeList = LocaleList("fr")))
                append("Bonjour!\n")
                pop()
                pushStyle(SpanStyle(localeList = LocaleList("tr-TR")))
                append("Merhaba!\n")
                pop()
                pushStyle(SpanStyle(localeList = LocaleList("ja-JP")))
                append("こんにちは!\n")
                pop()
                pushStyle(SpanStyle(localeList = LocaleList("zh")))
                append("你好!")
                pop()
            },
//            style = TextStyle(fontSize = fontSize8)
        )

//        TagLine("VerbatimTtsAnnotation ")
        Text(
            text = buildAnnotatedString {
                append("This word is read verbatim: ")
                pushTtsAnnotation(VerbatimTtsAnnotation(verbatim = "hello"))
                append("hello\n")
                pop()
                append("This word is read normally: hello")
            },
//            style = TextStyle(fontSize = fontSize8)
        )

//        TagLine("UrlAnnotation")
        Text(
            text = buildAnnotatedString {
                append("This word is a link: ")
                //包含用于文本到语音引擎的元数据的注释。如果文本正在由文本转语音引擎处理，则引擎可以使用此注释中的数据来补充或代替其关联的文本。
                withAnnotation(UrlAnnotation("https://google.com")) {
                    append("Google\n")
                }
                append("This word is not a link: google.com")
            },
//            style = TextStyle(fontSize = fontSize8)
        )
    }
}

/**
 * Version 1.3.0-alpha01新添加
 *为 TextStyle 和 SpanStyle 的 Brush 风格添加可选的 alpha 参数以修改整个文本的不透明度。
 **/
@OptIn(ExperimentalTextApi::class)
@Composable
fun TextBrushAlpha() {
    Text(
        text = "sdkla;sd",
        style = TextStyle(
            brush = Brush.linearGradient(listOf(Color.Red, Color.Blue, Color.Green)),
            alpha = 0.1f
        )
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun SpanStyleBrushSample() {
    val brushColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow)
    Text(
        fontSize = 16.sp,
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = Brush.radialGradient(brushColors)
                )
            ) {
                append("Hello")
            }
            withStyle(
                SpanStyle(
                    brush = Brush.radialGradient(brushColors.asReversed())
                )
            ) {
                append(" World")
            }
        }
    )
}

private const val SINGLE_LINE_ITEM = "A single line to test."
private const val MULTIPLE_LINE_ITEM =
    "This is a multi-line string to test on the item for staggering."
private val listOfItems = listOf(
    MULTIPLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    SINGLE_LINE_ITEM,
    MULTIPLE_LINE_ITEM,
)