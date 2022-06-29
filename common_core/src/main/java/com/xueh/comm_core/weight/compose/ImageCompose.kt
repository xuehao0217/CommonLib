package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ImageLoadCompose(
    url: String, modifier: Modifier = Modifier, placeholder: Painter? = null,
    error: Painter? = null, onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) = AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .build(),
    modifier = modifier,
    contentDescription = null,
    placeholder = placeholder, //加载中展位图
    error = error,
    onSuccess = onSuccess //加载成功
)

@Composable
fun ImageCompose(@DrawableRes id: Int) =
    Image(painter = painterResource(id = id), contentDescription = "ImageComposeContentDescription")