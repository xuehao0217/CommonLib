package com.xueh.commonlib.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.xueh.comm_core.weight.compose.*
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2023/3/23
 * 备注：
 */

@Preview
@Composable
fun CommonTabPage() {
    CommonTabPage(tabsName = mutableListOf("AAAAAAA", "BBBBBBB")) { index ->
        if (index == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Yellow)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
// [START android_compose_carousel_multi_browse_basic]
@Composable
fun CarouselExample_MultiBrowse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.cupcake, "cupcake"),
            CarouselItem(1, R.drawable.donut, "donut"),
            CarouselItem(2, R.drawable.eclair, "eclair"),
            CarouselItem(3, R.drawable.froyo, "froyo"),
            CarouselItem(4, R.drawable.gingerbread, "gingerbread"),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CarouselExample() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val carouselItems = remember {
        listOf(
            CarouselItem(0, R.drawable.cupcake, "cupcake"),
            CarouselItem(1, R.drawable.donut, "donut"),
            CarouselItem(2, R.drawable.eclair, "eclair"),
            CarouselItem(3, R.drawable.froyo, "froyo"),
            CarouselItem(4, R.drawable.gingerbread, "gingerbread"),
        )
    }

    HorizontalUncontainedCarousel(
        state = rememberCarouselState { carouselItems.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        itemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = carouselItems[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}


@Preview
@Composable
fun CarouselExamples() {
    Column {
        CarouselExample()
        CarouselExample_MultiBrowse()
    }
}