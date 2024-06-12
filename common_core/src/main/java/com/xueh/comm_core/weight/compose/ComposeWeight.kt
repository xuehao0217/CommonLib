package com.xueh.comm_core.weight.compose

import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.compose.rememberMutableStateOf
import com.xueh.comm_core.helper.isEmpty
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader
import com.xueh.comm_core.weight.compose.refreshheader.RefreshHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//加载图片
// https://coil-kt.github.io/coil/compose/
// https://github.com/coil-kt/coil/blob/main/README-zh.md
@Composable
fun ImageLoadAsyncImage(
    url: String, modifier: Modifier = Modifier, placeholder: Painter? = null,
    error: Painter? = null, onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) = AsyncImage(
    model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
    modifier = modifier,
    contentScale = ContentScale.Crop,
    contentDescription = null,
    placeholder = placeholder, //加载中展位图
    error = error,
    onSuccess = onSuccess //加载成功
)


@Composable
fun ImageLoadCompose(
    url: String,
    modifier: Modifier = Modifier,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
        modifier = modifier,
        contentScale = ContentScale.Crop,
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

//公用列表
@Composable
fun CommonLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    content: LazyListScope.() -> Unit,
) {

    ConstraintLayout(modifier = modifier) {
        var (column, bottom_v) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(column) {
                    top.linkTo(parent.top)
                },
            state = state,
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding,
        ) {
            item {
                headContent.invoke()
            }
            content(this)
            item {
                foodContent.invoke()
            }
        }
        ShadowVerticalView(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(bottom_v) {
                bottom.linkTo(parent.bottom)
            })
    }
}

//公用数据列表
@Composable
fun <T> CommonLazyColumnDatas(
    datas: List<T>,
    modifier: Modifier = Modifier.fillMaxSize(),
    state: LazyListState = rememberLazyListState(),
    key: ((item: T) -> Any)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {

    CommonLazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        state = state,
        contentPadding = contentPadding,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        items(datas, key = key) {
            itemContent(it)
        }
    }
}


//公用下拉刷新页面
@Composable
fun CommonRefreshPage(
    isRefreshing: Boolean,
    onRefresh: (suspend () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
//    var refreshing by remember { mutableStateOf(false) }
    var refreshState = rememberSmartSwipeRefreshState()
    var listState = rememberLazyListState()
    SmartRefresh(
        isRefreshing = isRefreshing,
        scrollState = listState,
        refreshState = refreshState,
        headerIndicator = { RefreshHeader(refreshState) },
        onRefresh = onRefresh,
        content = content
    )
}


//公用下拉刷新列表数据页面
@Composable
fun <T> CommonRefreshColumnDataPage(
    datas: List<T>,
    isRefreshing: Boolean,
    onRefresh: (suspend () -> Unit)? = null,
    emptContent: @Composable () -> Unit? = {},
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
//    var refreshing by remember { mutableStateOf(false) }
    var refreshState = rememberSmartSwipeRefreshState()
    var listState = rememberLazyListState()
    SmartRefresh(
        isRefreshing = isRefreshing,
        scrollState = listState,
        refreshState = refreshState,
        headerIndicator = { MyRefreshHeader(refreshState) },
        onRefresh = onRefresh
    ) {
        if (datas.isEmpty()) {
            emptContent?.invoke()
        } else {
            CommonLazyColumnDatas(
                datas = datas,
                headContent = headContent,
                foodContent = foodContent,
                itemContent = itemContent
            )
        }
    }
}

//公用 下拉刷新下拉加载 页面
@Composable
fun <T : Any> CommonPagingPage(
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    isFirstRefresh: Boolean = true,
    enableRefresh: Boolean = true,

    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),

    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    onScrollStop: ((visibleItem: List<Int>, isScrollingUp: Boolean) -> Unit)? = null,

    emptyDataContent: (@Composable BoxScope.() -> Unit)? = null,
    loadingContent: (@Composable BoxScope.() -> Unit)? = null,

    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},

    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    var lastFirstIndex by rememberMutableStateOf(value = 0)
    var isScrollingUp = false
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }.collect { isScrolling ->
            if (!isScrolling) {
                if (lazyListState.firstVisibleItemIndex > lastFirstIndex) {
                    // 上滑
                    isScrollingUp = true
                } else {
                    //下滑
                    isScrollingUp = false
                }
                lastFirstIndex = lazyListState.firstVisibleItemIndex
                // 滑动停止
                val visibleItemsIndex =
                    lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toList()
                onScrollStop?.invoke(visibleItemsIndex, isScrollingUp)
            }
        }
    }
    RefreshList(
        enableRefresh = enableRefresh,
        isFirstRefresh = isFirstRefresh,
        lazyListState = lazyListState,
        refreshState = refreshState,
        lazyPagingItems = lazyPagingItems,
        headerIndicator = headerIndicator,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        emptyDataContent=emptyDataContent,
        loadingContent=loadingContent,
        headContent=headContent,
        foodContent = foodContent,
    ) {
        // 如果是老版本的Paging3这里的实现方式不同，自己根据版本来实现。
        items(lazyPagingItems.itemCount, key = { it }) { index ->
            lazyPagingItems[index]?.let {
                itemContent(it)
            }
        }
    }
}


//公用带滑动Tab页面
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommonTabPage(tabsName: List<String>, pageContent: @Composable (page: Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = {
            tabsName.size
        })
        TabRow(modifier = Modifier
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

        HorizontalPager(state = pagerState, Modifier.weight(1f)) { page ->
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
    input = it.uppercase()
},
keyboardOptions = KeyboardOptions(
keyboardType = KeyboardType.Text,
capitalization = KeyboardCapitalization.Characters,
  imeAction = ImeAction.Search,//键盘右下角
),
maxLength = 7,
textFieldPadding = 0,
textFieldEnabled = textFieldEnabled,
hintText = "Please enter keywords",
hintTextColor = cl_999999,
hintTextSize = 12.sp,
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
    hintText: String = "",
    hintTextColor: Color = Color(0xFF999999),
    hintTextSize: TextUnit = 12.sp,
    @DrawableRes delIconId: Int,
    delIconEndDP: Int = 9,
    delIconSize: Dp = 8.dp,
    text: String = "",
    textColor: Color = Color.Unspecified,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
    textFieldPadding: Int = 34,
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
            textStyle = TextStyle(
                fontSize = textSize,
                fontWeight = FontWeight(400),
                color = textColor,
            ),
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange.invoke(it)
                }
                showDel = it.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = textFieldPadding.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = 1,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = hintText, color = hintTextColor, fontSize = hintTextSize,
                        )
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
                SpacerW(int = delIconEndDP)
            }

        }


    }
}


@Composable
fun HighlightedText(
    text: String,
    textColor: Color = Color.Unspecified,
    highlight: String,
    highlightColor: Color = Color.Unspecified,
    modifier: Modifier
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
        fontSize = 16.sp,
        modifier = modifier,
        color = textColor,
    )
}